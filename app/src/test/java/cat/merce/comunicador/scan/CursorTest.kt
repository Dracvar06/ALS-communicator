package cat.merce.comunicador.scan

import org.junit.Assert.assertEquals
import org.junit.Test

class CursorTest {

    /** Three rows of different lengths, so the ragged cases are always covered. */
    private fun ragged() = Cursor(ScanLayout(listOf(6, 4, 6)))

    @Test
    fun `starts at the top left`() {
        assertEquals(Position(0, 0), ragged().position)
    }

    @Test
    fun `right moves across the row`() {
        val cursor = ragged()
        cursor.right()
        cursor.right()
        assertEquals(Position(0, 2), cursor.position)
    }

    @Test
    fun `right wraps to the start of the same row`() {
        val cursor = Cursor(ScanLayout(listOf(3)))
        repeat(3) { cursor.right() }
        assertEquals(Position(0, 0), cursor.position)
    }

    @Test
    fun `left from the first cell wraps to the last of the same row`() {
        val cursor = Cursor(ScanLayout(listOf(3)))
        cursor.left()
        assertEquals(Position(0, 2), cursor.position)
    }

    @Test
    fun `down moves to the row below`() {
        val cursor = ragged()
        cursor.down()
        assertEquals(Position(1, 0), cursor.position)
    }

    @Test
    fun `down from the last row wraps to the top`() {
        val cursor = ragged()
        repeat(3) { cursor.down() }
        assertEquals(Position(0, 0), cursor.position)
    }

    @Test
    fun `up from the top row wraps to the bottom`() {
        val cursor = ragged()
        cursor.up()
        assertEquals(Position(2, 0), cursor.position)
    }

    @Test
    fun `moving into a shorter row lands on its last cell`() {
        val cursor = ragged()
        repeat(5) { cursor.right() }
        assertEquals(Position(0, 5), cursor.position)

        cursor.down()
        // Row 1 only has four cells, so column five does not exist there.
        assertEquals(Position(1, 3), cursor.position)
    }

    @Test
    fun `the column she aimed for survives a shorter row`() {
        val cursor = ragged()
        repeat(5) { cursor.right() }
        cursor.down()
        cursor.down()

        // This is the whole point of remembering the desired column: passing
        // through a four-cell row must not cost her the sixth column for good.
        assertEquals(Position(2, 5), cursor.position)
    }

    @Test
    fun `moving sideways in a short row forgets the old aim`() {
        val cursor = ragged()
        repeat(5) { cursor.right() }
        cursor.down()
        // She has now chosen column three deliberately, so that is the new aim.
        cursor.left()
        cursor.down()

        assertEquals(Position(2, 2), cursor.position)
    }

    @Test
    fun `moveTo clamps into the grid`() {
        val cursor = ragged()
        cursor.moveTo(1, 99)
        assertEquals(Position(1, 3), cursor.position)

        cursor.moveTo(99, 0)
        assertEquals(Position(2, 0), cursor.position)
    }

    @Test
    fun `a single cell grid never moves anywhere`() {
        val cursor = Cursor(ScanLayout(listOf(1)))
        cursor.right(); cursor.left(); cursor.up(); cursor.down()
        assertEquals(Position(0, 0), cursor.position)
    }
}
