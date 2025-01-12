import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import assignments.ex2.CellEntry;
import assignments.ex2.Ex2Utils;

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
        assertEquals(25, cell.getX());
        assertEquals(99, cell.getY());
    }

    @Test
    public void testConstructorWithNullString() {
        CellEntry cell = new CellEntry(null);
        assertEquals(-1, cell.getX());
        assertEquals(-1, cell.getY());
    }

    @Test
    public void testConstructorWithEmptyString() {
        CellEntry cell = new CellEntry("");
        assertEquals(-1, cell.getX());
        assertEquals(-1, cell.getY());
    }

    @Test
    public void testConstructorWithLowerCaseString() {
        CellEntry cell = new CellEntry("b3");
        assertEquals(1, cell.getX());
        assertEquals(3, cell.getY());
    }

    @Test
    public void testIsValid() {
        CellEntry validCell = new CellEntry(1, 2);
        assertTrue(validCell.isValid());

        CellEntry invalidCell = new CellEntry(-1, -1);
        assertFalse(invalidCell.isValid());
    }

    @Test
    public void testIsValidEdgeCases() {
        CellEntry cell = new CellEntry(Ex2Utils.WIDTH - 1, Ex2Utils.HEIGHT - 1);
        assertTrue(cell.isValid());

        cell = new CellEntry(Ex2Utils.WIDTH, Ex2Utils.HEIGHT);
        assertFalse(cell.isValid());

        cell = new CellEntry(-1, -1);
        assertFalse(cell.isValid());
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

    @Test
    public void testToStringEdgeCases() {
        CellEntry cell = new CellEntry(Ex2Utils.WIDTH - 1, Ex2Utils.HEIGHT - 1);
        assertEquals((char) ('A' + Ex2Utils.WIDTH - 1) + Integer.toString(Ex2Utils.HEIGHT - 1), cell.toString());

        cell = new CellEntry(Ex2Utils.WIDTH, Ex2Utils.HEIGHT);
        assertEquals("Invalid Index", cell.toString());
    }
}