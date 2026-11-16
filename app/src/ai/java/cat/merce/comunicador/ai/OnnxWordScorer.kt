package cat.merce.comunicador.ai

import ai.onnxruntime.OnnxTensor
import ai.onnxruntime.OrtEnvironment
import ai.onnxruntime.OrtSession
import cat.merce.comunicador.prediction.WordScorer
import java.nio.FloatBuffer
import java.nio.LongBuffer
import kotlin.math.exp
import kotlin.math.ln

/**
 * Scores candidate words with a small language model (Gemma 3 270M, int8)
 * running on the tablet's processor through ONNX Runtime.
 *
 * A word's score is log P(its tokens, then the start of another word | the
 * message so far), so a long word made of several tokens is judged as a whole.
 *
 * The message so far is run through the model once and its cache kept; each
 * keystroke then only runs the few tokens of the candidates on top of it.
 *
 * Called from one background thread only. Never from the thread that draws.
 */
class OnnxWordScorer internal constructor(
    private val env: OrtEnvironment,
    private val session: OrtSession,
    private val meta: AiMeta,
    private val prompt: IntArray,
    private val tokens: AiTokens,
) : WordScorer, AutoCloseable {

    private val pastNames = (0 until meta.layers).flatMap { listOf("past_key_values.$it.key", "past_key_values.$it.value") }
    private val presentNames = (0 until meta.layers).flatMap { listOf("present.$it.key", "present.$it.value") }

    /** The message last run, its cache (one array per past input) and its next-token log-probabilities. */
    private var cachedIds: IntArray = IntArray(0)
    private var cachedPast: List<FloatArray> = emptyList()
    private var cachedFirst: FloatArray? = null

    override fun score(previousWords: List<String>, candidates: List<String>): Map<String, Double>? {
        val context = prompt + previousWords.flatMap { tokens.of(it)?.asList() ?: emptyList() }
        val first = contextLogProbs(context) ?: return null

        val seqs = candidates.mapNotNull { word -> tokens.of(word)?.let { word to it } }
            .filter { it.second.isNotEmpty() }
        if (seqs.isEmpty()) return null
        val n = seqs.size
        val len = seqs.maxOf { it.second.size }
        val pastLen = cachedIds.size

        val ids = LongArray(n * len)
        val mask = LongArray(n * (pastLen + len))
        seqs.forEachIndexed { j, (_, t) ->
            t.forEachIndexed { k, id -> ids[j * len + k] = id.toLong() }
            for (k in 0 until pastLen + t.size) mask[j * (pastLen + len) + k] = 1
        }

        val inputs = HashMap<String, OnnxTensor>()
        try {
            inputs["input_ids"] = OnnxTensor.createTensor(env, LongBuffer.wrap(ids), longArrayOf(n.toLong(), len.toLong()))
            inputs["attention_mask"] = OnnxTensor.createTensor(env, LongBuffer.wrap(mask), longArrayOf(n.toLong(), (pastLen + len).toLong()))
            val rowSize = meta.kvHeads * pastLen * meta.headDim
            pastNames.forEachIndexed { i, name ->
                // The same cache for every candidate: repeat it once per row.
                val one = cachedPast[i]
                val all = FloatArray(n * rowSize)
                for (j in 0 until n) System.arraycopy(one, 0, all, j * rowSize, rowSize)
                inputs[name] = OnnxTensor.createTensor(env, FloatBuffer.wrap(all), shape(n, pastLen))
            }
            session.run(inputs, setOf("logits")).use { result ->
                val logits = (result.get("logits").get() as OnnxTensor).floatBuffer
                val v = meta.vocabSize
                val row = FloatArray(v)
                val out = HashMap<String, Double>(n * 2)
                seqs.forEachIndexed { j, (word, t) ->
                    var total = first[t[0]].toDouble()
                    for (k in t.indices) {
                        logits.position((j * len + k) * v)
                        logits.get(row, 0, v)
                        val lse = logSumExp(row)
                        total += if (k + 1 < t.size) {
                            (row[t[k + 1]] - lse).toDouble()
                        } else {
                            // the word ends here: a new word starts next
                            ln(boundaryMass(row, lse) + 1e-12)
                        }
                    }
                    out[word] = total
                }
                return out
            }
        } finally {
            inputs.values.forEach { it.close() }
        }
    }

    /**
     * Log-probabilities of the token after [context], updating the cache. Only
     * the tokens added since last time are run, which after the first word of
     * a message is usually one or two.
     */
    private fun contextLogProbs(context: IntArray): FloatArray? {
        if (context.contentEquals(cachedIds)) return cachedFirst
        val keep = if (cachedIds.isNotEmpty() && context.size > cachedIds.size &&
            context.copyOfRange(0, cachedIds.size).contentEquals(cachedIds)
        ) cachedIds.size else 0
        val fresh = context.copyOfRange(keep, context.size)

        val inputs = HashMap<String, OnnxTensor>()
        try {
            inputs["input_ids"] = OnnxTensor.createTensor(env, LongBuffer.wrap(fresh.map { it.toLong() }.toLongArray()), longArrayOf(1, fresh.size.toLong()))
            inputs["attention_mask"] = OnnxTensor.createTensor(env, LongBuffer.wrap(LongArray(context.size) { 1 }), longArrayOf(1, context.size.toLong()))
            pastNames.forEachIndexed { i, name ->
                val past = if (keep == 0) FloatArray(0) else cachedPast[i]
                inputs[name] = OnnxTensor.createTensor(env, FloatBuffer.wrap(past), shape(1, keep))
            }
            session.run(inputs).use { result ->
                cachedPast = presentNames.map { name ->
                    val buf = (result.get(name).get() as OnnxTensor).floatBuffer
                    FloatArray(buf.remaining()).also { buf.get(it) }
                }
                val logits = (result.get("logits").get() as OnnxTensor).floatBuffer
                val v = meta.vocabSize
                val last = FloatArray(v)
                logits.position((fresh.size - 1) * v)
                logits.get(last, 0, v)
                val lse = logSumExp(last)
                for (i in last.indices) last[i] -= lse
                cachedIds = context
                cachedFirst = last
                return last
            }
        } catch (e: Exception) {
            cachedIds = IntArray(0)
            cachedPast = emptyList()
            cachedFirst = null
            return null
        } finally {
            inputs.values.forEach { it.close() }
        }
    }

    private fun shape(batch: Int, past: Int) =
        longArrayOf(batch.toLong(), meta.kvHeads.toLong(), past.toLong(), meta.headDim.toLong())

    private fun logSumExp(x: FloatArray): Float {
        var max = Float.NEGATIVE_INFINITY
        for (value in x) if (value > max) max = value
        var sum = 0.0
        for (value in x) sum += exp((value - max).toDouble())
        return (max + ln(sum)).toFloat()
    }

    private fun boundaryMass(row: FloatArray, lse: Float): Double {
        var p = 0.0
        for (i in row.indices) if (tokens.startsWord(i)) p += exp((row[i] - lse).toDouble())
        return p
    }

    override fun close() {
        session.close()
    }
}
