package cat.merce.comunicador.ai

import android.content.Context
import cat.merce.comunicador.prediction.WordScorer

/**
 * The standard build's half of the switch between builds: no model, no AI
 * library, nothing to load. The settings screen hides the AI option when
 * [AVAILABLE] is false, so this build looks and behaves exactly as before.
 */
object AiEngine {

    const val AVAILABLE = false

    @Suppress("UNUSED_PARAMETER")
    fun refusal(context: Context): String? = "not-in-this-build"

    @Suppress("UNUSED_PARAMETER")
    fun open(context: Context, language: String): WordScorer? = null

    @Suppress("UNUSED_PARAMETER")
    fun close(scorer: WordScorer?) = Unit
}
