package cat.merce.comunicador.ai

import ai.onnxruntime.OrtEnvironment
import ai.onnxruntime.OrtSession
import android.app.ActivityManager
import android.content.Context
import cat.merce.comunicador.prediction.WordScorer
import org.json.JSONObject
import java.io.File
import java.util.BitSet

/**
 * The AI build's half of the switch between builds. The standard build has a
 * file of the same name that offers nothing; the rest of the app only ever
 * asks [AiEngine], so it compiles the same either way.
 */
object AiEngine {

    /** This build carries the model, so the setting is shown. */
    const val AVAILABLE = true

    /**
     * A device with less memory than this is not offered the AI at all: the
     * model needs about 600 MB while running, and on a small device that
     * memory would be taken from everything else.
     */
    private const val MIN_TOTAL_MEMORY = 3_000_000_000L

    /** Why the AI cannot run on this device, or null if it can. */
    fun refusal(context: Context): String? {
        val am = context.getSystemService(ActivityManager::class.java)
        val info = ActivityManager.MemoryInfo().also { am.getMemoryInfo(it) }
        return when {
            am.isLowRamDevice -> "low-ram"
            info.totalMem < MIN_TOTAL_MEMORY -> "memory"
            else -> null
        }
    }

    /**
     * Loads the model. Slow (seconds, and the first time a copy of about
     * 450 MB), so call it off the main thread. Returns null on any failure,
     * which leaves the app on its normal suggestions.
     */
    fun open(context: Context, language: String): WordScorer? = runCatching {
        if (refusal(context) != null) return null
        val assets = context.assets
        val model = File(context.noBackupFilesDir, "ai-model.onnx")
        val size = assets.openFd("ai-model/model.onnx").use { it.length }
        if (!model.exists() || model.length() != size) {
            // Copied once to a real file, because the model is far too big to
            // hold in memory as bytes just to hand it to the runtime.
            val partial = File(model.path + ".part")
            assets.open("ai-model/model.onnx").use { input ->
                partial.outputStream().use { input.copyTo(it, 1 shl 20) }
            }
            if (!partial.renameTo(model)) return null
        }

        val metaJson = JSONObject(assets.open("ai-model/meta.json").bufferedReader().readText())
        val meta = AiMeta(
            vocabSize = metaJson.getInt("vocabSize"),
            layers = metaJson.getInt("layers"),
            kvHeads = metaJson.getInt("kvHeads"),
            headDim = metaJson.getInt("headDim"),
        )
        val prompts = metaJson.getJSONObject("prompts")
        val promptJson = prompts.optJSONArray(language) ?: prompts.getJSONArray("ca")
        val prompt = IntArray(promptJson.length()) { promptJson.getInt(it) }

        val words = HashMap<String, IntArray>()
        val wordsAsset = "ai-model/words-$language.tsv"
        assets.open(if (assets.list("ai-model")?.contains("words-$language.tsv") == true) wordsAsset else "ai-model/words-ca.tsv")
            .bufferedReader().forEachLine { line ->
                val tab = line.indexOf('\t')
                if (tab > 0) {
                    words[line.substring(0, tab)] = line.substring(tab + 1).split(' ').map { it.toInt() }.toIntArray()
                }
            }
        val pieces = HashMap<String, Int>()
        assets.open("ai-model/pieces.tsv").bufferedReader().forEachLine { line ->
            val tab = line.lastIndexOf('\t')
            if (tab > 0) pieces.putIfAbsent(line.substring(0, tab), line.substring(tab + 1).toInt())
        }
        val boundary = BitSet.valueOf(assets.open("ai-model/boundary.bin").readBytes())

        val env = OrtEnvironment.getEnvironment()
        // No usage reports. The app has no network permission anyway (see the
        // AI build's manifest), but nothing should even try.
        runCatching { env.setTelemetry(false) }
        val options = OrtSession.SessionOptions().apply {
            // Two threads: enough to answer in time on a decent tablet, and
            // leaves the rest for drawing the grid and everything else.
            setIntraOpNumThreads(2)
            setOptimizationLevel(OrtSession.SessionOptions.OptLevel.ALL_OPT)
        }
        val session = env.createSession(model.path, options)
        OnnxWordScorer(env, session, meta, prompt, AiTokens(words, pieces, boundary))
    }.getOrNull()

    /** Frees the model's memory when the AI is turned off. */
    fun close(scorer: WordScorer?) {
        (scorer as? AutoCloseable)?.close()
    }
}

internal data class AiMeta(val vocabSize: Int, val layers: Int, val kvHeads: Int, val headDim: Int)

/**
 * Words to the model's tokens, without a tokenizer on the device. Words in the
 * n-gram vocabulary come from a table built on the laptop
 * (tools/build_ai_assets.py); anything else, such as a name, is split
 * greedily into the longest known pieces. Greedy is not always how the model
 * would split it, but it is a valid split, which is all a score needs.
 */
internal class AiTokens(
    private val words: Map<String, IntArray>,
    private val pieces: Map<String, Int>,
    private val boundary: BitSet,
) {
    private val longestPiece = pieces.keys.maxOfOrNull { it.length } ?: 1

    fun startsWord(id: Int): Boolean = boundary.get(id)

    /** Tokens of " word", or null if it cannot be split at all. */
    fun of(word: String): IntArray? {
        val key = word.lowercase()
        words[key]?.let { return it }
        val text = " $key"
        val out = ArrayList<Int>()
        var at = 0
        while (at < text.length) {
            var found = -1
            var end = minOf(text.length, at + longestPiece)
            while (end > at) {
                val id = pieces[text.substring(at, end)]
                if (id != null) {
                    found = id
                    break
                }
                end--
            }
            if (found < 0) return null
            out += found
            at = end
        }
        return out.toIntArray()
    }
}
