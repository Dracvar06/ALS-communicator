package cat.merce.comunicador.ui

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/** The questions a fresh install asks before the walkthrough. */
class OnboardingTest {

    private fun atMethod(): ScanController {
        val c = ScanController()
        c.openOnboarding()
        c.startOnboarding()
        return c
    }

    /** Assigns one button to whatever role is waiting, then says done. */
    private fun ScanController.assign(label: String) {
        val role = bindingRole!!
        addedBinding(role, label)
        finishBinding()
    }

    @Test
    fun `it starts at the welcome and moves on to the method question`() {
        val c = ScanController()
        c.openOnboarding()
        assertEquals(OnboardingStep.Welcome, c.onboardingStep)
        c.startOnboarding()
        assertEquals(OnboardingStep.Method, c.onboardingStep)
    }

    @Test
    fun `touching the letters needs no more questions`() {
        val c = atMethod()
        c.chooseMethod(InputMode.Direct)
        assertEquals(InputMode.Direct, c.inputMode)
        assertFalse(c.inOnboarding)
        assertTrue(c.inTutorial)
    }

    @Test
    fun `arrows ask where the pad goes`() {
        val c = atMethod()
        c.chooseMethod(InputMode.Arrows)
        assertEquals(OnboardingStep.ArrowPlace, c.onboardingStep)
        c.chooseArrowPlace(ArrowPlacement.Bottom)
        assertEquals(ArrowPlacement.Bottom, c.arrowPlacement)
        assertTrue(c.inTutorial)
    }

    @Test
    fun `scanning on the screen keeps touch on`() {
        val c = atMethod()
        c.chooseMethod(InputMode.Scan)
        assertEquals(OnboardingStep.ScanPress, c.onboardingStep)
        c.chooseScanPress(ScanPress.Screen)
        assertTrue(c.touchInput)
        assertTrue(c.inTutorial)
    }

    @Test
    fun `switches assign write, then undo, then turn touch off`() {
        val c = atMethod()
        c.chooseMethod(InputMode.Scan)
        c.chooseScanPress(ScanPress.Switches)

        assertEquals(SwitchRole.Write, c.bindingRole)
        c.assign("SPACE")
        assertEquals(SwitchRole.Undo, c.bindingRole)
        // Touch must still work until both switches are in place.
        assertTrue(c.touchInput)
        c.assign("ENTER")

        assertFalse(c.touchInput)
        assertNull(c.bindingRole)
        assertTrue(c.inTutorial)
    }

    @Test
    fun `both keeps touch on after the switches are assigned`() {
        val c = atMethod()
        c.chooseMethod(InputMode.Scan)
        c.chooseScanPress(ScanPress.Both)
        c.assign("SPACE")
        c.assign("ENTER")
        assertTrue(c.touchInput)
        assertTrue(c.inTutorial)
    }

    @Test
    fun `giving up on the switches goes back to the question with touch still on`() {
        val c = atMethod()
        c.chooseMethod(InputMode.Scan)
        c.chooseScanPress(ScanPress.Switches)
        c.assign("SPACE")
        c.cancelBinding()

        assertTrue(c.touchInput)
        assertEquals(OnboardingStep.ScanPress, c.onboardingStep)
        assertFalse(c.inTutorial)
    }

    @Test
    fun `back steps to the previous question`() {
        val c = atMethod()
        c.chooseMethod(InputMode.Arrows)
        c.onboardingBack()
        assertEquals(OnboardingStep.Method, c.onboardingStep)
        c.onboardingBack()
        assertEquals(OnboardingStep.Welcome, c.onboardingStep)
    }

    @Test
    fun `either switch leaves onboarding for the grid`() {
        val write = atMethod()
        write.press()
        assertFalse(write.inOnboarding)
        assertFalse(write.inTutorial)

        val undo = atMethod()
        undo.undo()
        assertFalse(undo.inOnboarding)
        assertFalse(undo.inTutorial)
    }

    @Test
    fun `the scan does not move during onboarding`() {
        val c = atMethod()
        val before = c.state
        c.tick()
        c.tick()
        assertEquals(before, c.state)
    }

    @Test
    fun `the walkthrough shows only the pages for the chosen method`() {
        for (mode in InputMode.entries) {
            val c = ScanController()
            c.useInputMode(mode)
            assertTrue(c.tutorialPages.all { mode in it.modes })
            assertTrue(c.tutorialPages.size < c.language.tutorial.size)
        }
    }

    @Test
    fun `every language has a walkthrough page for every method`() {
        for (language in LANGUAGES) {
            for (mode in InputMode.entries) {
                val specific = language.tutorial.filter { it.modes == setOf(mode) }
                assertEquals("${language.code} $mode", 1, specific.size)
            }
        }
    }

    @Test
    fun `every language has its own onboarding text`() {
        for (language in LANGUAGES) {
            if (language == ENGLISH) continue
            assertTrue(
                "${language.code} still has the English onboarding",
                language.onboarding.welcomeTitle != ENGLISH.onboarding.welcomeTitle,
            )
        }
    }

    /** Touches each letter of [word] on the grid, in direct mode. */
    private fun ScanController.touchWord(word: String) {
        for (letter in word) {
            val (row, col) = rows.withIndex().firstNotNullOf { (r, keys) ->
                keys.indices.firstOrNull { label(keys[it]) == letter.toString() }?.let { r to it }
            }
            touchKey(row, col)
        }
    }

    /** Fresh install, touch method, walkthrough read to the end. */
    private fun throughSetup(): ScanController {
        val c = atMethod()
        c.chooseMethod(InputMode.Direct)
        repeat(c.tutorialPages.size) { c.nextTutorialPage() }
        return c
    }

    @Test
    fun `setup ends in practice, not in the grid`() {
        val c = throughSetup()
        assertFalse(c.inTutorial)
        assertEquals(PracticeStep.Write, c.practiceStep)
        assertTrue(c.setupInProgress)
    }

    @Test
    fun `practice wants the word, then one letter undone`() {
        val c = throughSetup()
        c.touchWord(c.practiceWord)
        assertEquals(PracticeStep.Undo, c.practiceStep)
        c.undo()
        assertEquals(PracticeStep.Done, c.practiceStep)
        c.finishPractice()
        assertNull(c.practiceStep)
        assertFalse(c.setupInProgress)
        assertEquals("", c.text)
    }

    @Test
    fun `a wrong letter does not finish the word step`() {
        val c = throughSetup()
        c.touchWord(c.practiceWord.dropLast(1))
        assertEquals(PracticeStep.Write, c.practiceStep)
    }

    @Test
    fun `practice from settings gives back what was being written`() {
        val c = ScanController()
        c.useInputMode(InputMode.Direct)
        c.touchWord("SI")
        val before = c.text
        c.openSettings()
        c.startPractice()
        assertFalse(c.inSettings)
        assertEquals("", c.text)
        c.finishPractice()
        assertEquals(before, c.text)
    }

    @Test
    fun `a switch during the setup walkthrough still leaves, and ends setup`() {
        val c = atMethod()
        c.chooseMethod(InputMode.Direct)
        assertTrue(c.setupInProgress)
        c.press()
        assertFalse(c.inTutorial)
        assertFalse(c.setupInProgress)
        assertNull(c.practiceStep)
    }

    @Test
    fun `every practice word can be typed on its own grid`() {
        for (language in LANGUAGES) {
            val keys = language.letterRows.flatten().toSet()
            for (letter in language.onboarding.practiceWord) {
                assertTrue("${language.code}: $letter", letter.toString() in keys)
            }
        }
    }
}
