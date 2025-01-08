import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import assignments.ex2.*;

import static org.junit.jupiter.api.Assertions.*;




public class SCellTest {

    private SCell cell;

    @BeforeEach
    public void setUp() {
        cell = new SCell("initial");
    }

    @Test
    public void testConstructor() {
        SCell cell = new SCell("initial");
        assertEquals("initial", cell.getData());
        assertEquals(Ex2Utils.TEXT, cell.getType());
    }

    @Test
    public void testGetData() {
        assertEquals("initial", cell.getData());
    }

    @Test
    public void testSetData() {
        cell.setData("123");
        assertEquals("123", cell.getData());
        assertEquals(Ex2Utils.NUMBER, cell.getType());

        cell.setData("=SUM(A1:B2)");
        assertEquals("=SUM(A1:B2)", cell.getData());
        assertEquals(Ex2Utils.FORM, cell.getType());

        cell.setData("text");
        assertEquals("text", cell.getData());
        assertEquals(Ex2Utils.TEXT, cell.getType());
    }

    @Test
    public void testIsNumber() {
        assertTrue(cell.isNumber("123"));
        assertTrue(cell.isNumber("123.45"));
        assertFalse(cell.isNumber("text"));
        assertFalse(cell.isNumber("=SUM(A1:B2)"));
    }

    @Test
    public void testGetType() {
        assertEquals(Ex2Utils.TEXT, cell.getType());
    }

    @Test
    public void testSetType() {
        cell.setType(Ex2Utils.NUMBER);
        assertEquals(Ex2Utils.NUMBER, cell.getType());
    }

    @Test
    public void testGetOrder() {
        cell.setOrder(1);
        assertEquals(1, cell.getOrder());
    }

    @Test
    public void testSetOrder() {
        cell.setOrder(2);
        assertEquals(2, cell.getOrder());
    }
}