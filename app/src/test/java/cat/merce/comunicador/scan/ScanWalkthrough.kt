package cat.merce.comunicador.scan

/**
 * A printed walkthrough of the scan machine, for reading rather than testing.
 *
 * This is a learning aid, not part of the app. It sits in the test sources, so
 * it is never compiled into anything that reaches the tablet. Run it and watch
 * the cursor move; delete it whenever it stops being useful.
 *
 * In Android Studio, click the green arrow next to `fun main` below.
 */

/**
 * Placeholder letters, chosen only so the grid has something in it. The real
 * Catalan frequency ordering is a later decision and does not belong here.
 */
private val DEMO_ROWS = listOf(
    listOf("A", "E", "S", "R"),
    listOf("N", "T", "O", "L"),
    listOf("I", "C", "D", "M"),
)

private val DEMO_LAYOUT = ScanLayout(DEMO_ROWS.map { it.size })

/** Draws the grid with whatever the cursor is currently on marked. */
private fun draw(state: ScanState) {
    val litRow = when (state) {
        is ScanState.Row -> state.row
        is ScanState.Cell -> state.row
    }

    for ((r, row) in DEMO_ROWS.withIndex()) {
        val arrow = if (r == litRow) ">" else " "
        val cells = row.mapIndexed { c, letter ->
            val isLitCell = state is ScanState.Cell && state.row == r && state.col == c
            if (isLitCell) "($letter)" else " $letter "
        }
        println("   $arrow  ${cells.joinToString("")}")
    }
    println()
}

private fun show(label: String, state: ScanState) {
    println(label)
    draw(state)
}

fun main() {
    println()
    println("=".repeat(58))
    println("SCENE 1  -  typing the letter O")
    println("=".repeat(58))
    println()
    println("A '>' marks the lit row. '(X)' marks the lit cell.")
    println()

    val s = Scanner(DEMO_LAYOUT, rowPassLimit = 2)
    show("start                       whole top row is lit", s.state)

    s.tick()
    show("tick                        moved down a row", s.state)

    var result = s.select()
    show("PRESS  -> $result           entered the row, on its first cell", s.state)

    s.tick()
    show("tick                        moved right", s.state)

    s.tick()
    show("tick                        moved right again", s.state)

    result = s.select()
    show("PRESS  -> $result", s.state)
    println("   ^ this is the only event that produces a letter.")
    println("     Position(row=1, col=2) means row 1, cell 2, which is O.")
    println("     The cursor is back at the top, ready for the next letter.")
    println()

    println("=".repeat(58))
    println("SCENE 2  -  pressing on the wrong row, and getting out")
    println("=".repeat(58))
    println()
    println("She meant to press on the bottom row, but pressed too early.")
    println()

    val m = Scanner(DEMO_LAYOUT, rowPassLimit = 2)
    m.select()
    show("PRESS                       stuck in the top row by mistake", m.state)

    repeat(4) { m.tick() }
    show("4 ticks                     one full sweep, wrapped to the start", m.state)

    repeat(3) { m.tick() }
    show("3 ticks                     part way through the second sweep", m.state)

    m.tick()
    show("tick                        second sweep done, so the row lets go", m.state)
    println("   ^ she is free again without pressing anything.")
    println("     With one switch there is no 'back' button, so the row has")
    println("     to release her by itself. That is the whole idea.")
    println()

    m.tick()
    show("tick                        carrying on from where she was", m.state)
}
