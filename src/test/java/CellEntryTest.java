import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import assignments.ex2.CellEntry;

public class CellEntryTest {

    @Test
    public void testConstructorWithIntegers() {
        CellEntry cell = new CellEntry(1, 2);
        assertEquals(1, cell.getX());
        assertEquals(2, cell.getY());
    }

    @Test
    public void testConstructorWithStringValid() {
        CellEntry cell = new CellEntry("B3");
        assertEquals(1, cell.getX());
        assertEquals(3, cell.getY());
    }

    @Test
    public void testConstructorWithStringInvalid() {
        CellEntry cell = new CellEntry("Z99");
        assertEquals(-1, cell.getX());
        assertEquals(-1, cell.getY());
    }

    @Test
    public void testConstructorWithNullString() {
        CellEntry cell = new CellEntry(null);
        assertEquals(-1, cell.getX());
        assertEquals(-1, cell.getY());
    }

    @Test
    public void testIsValid() {
        CellEntry validCell = new CellEntry(1, 2);
        assertTrue(validCell.isValid());

        CellEntry invalidCell = new CellEntry(-1, -1);
        assertFalse(invalidCell.isValid());
    }

    @Test
    public void testGetX() {
        CellEntry cell = new CellEntry(1, 2);
        assertEquals(1, cell.getX());
    }

    @Test
    public void testGetY() {
        CellEntry cell = new CellEntry(1, 2);
        assertEquals(2, cell.getY());
    }

    @Test
    public void testToStringValid() {
        CellEntry cell = new CellEntry(1, 2);
        assertEquals("B2", cell.toString());
    }

    @Test
    public void testToStringInvalid() {
        CellEntry cell = new CellEntry(-1, -1);
        assertEquals("Invalid Index", cell.toString());
    }
}