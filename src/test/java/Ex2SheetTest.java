import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import assignments.ex2.Ex2Sheet;
import assignments.ex2.Ex2Utils;

public class Ex2SheetTest {

    private Ex2Sheet sheet;

    @BeforeEach
    public void setUp() {
        sheet = new Ex2Sheet(5, 5);
    }

    @Test
    public void testConstructorWithDimensions() {
        Ex2Sheet sheet = new Ex2Sheet(3, 3);
        assertEquals(3, sheet.width());
        assertEquals(3, sheet.height());
    }

    @Test
    public void testDefaultConstructor() {
        Ex2Sheet sheet = new Ex2Sheet();
        assertEquals(Ex2Utils.WIDTH, sheet.width());
        assertEquals(Ex2Utils.HEIGHT, sheet.height());
    }

    @Test
    public void testIsIn() {
        assertTrue(sheet.isIn(0, 0));
        assertTrue(sheet.isIn(4, 4));
        assertFalse(sheet.isIn(5, 5));
        assertFalse(sheet.isIn(-1, -1));
    }

    @Test
    public void testWidth() {
        assertEquals(5, sheet.width());
    }

    @Test
    public void testHeight() {
        assertEquals(5, sheet.height());
    }

    @Test
    public void testSetAndGet() {
        sheet.set(1, 1, "123");
        assertEquals("123", sheet.get(1, 1).getData());
    }

    @Test
    public void testSetAndGetWithNullData() {
        sheet.set(1, 1, null);
        assertEquals(Ex2Utils.EMPTY_CELL, sheet.get(1, 1).getData());
    }

    @Test
    public void testGetWithInvalidCoordinates() {
        assertNull(sheet.get(5, 5));
    }

    @Test
    public void testGetWithStringCoordinates() {
        sheet.set(1, 1, "123");
        assertEquals("123", sheet.get("B1").getData());
    }

    @Test
    public void testGetWithInvalidStringCoordinates() {
        assertNull(sheet.get("Z9"));
    }

    @Test
    public void testValue() {
        sheet.set(1, 1, "123");
        assertEquals("123", sheet.value(1, 1));
    }

    @Test
    public void testValueWithEmptyCell() {
        assertEquals(Ex2Utils.EMPTY_CELL, sheet.value(1, 1));
    }

    @Test
    public void testEval() {
        sheet.set(1, 1, "123");
        assertEquals("123", sheet.eval(1, 1));
    }

    @Test
    public void testEvalWithFormula() {
        sheet.set(1, 1, "=2+3");
        assertEquals("5.0", sheet.eval(1, 1));
    }

    @Test
    public void testEvalWithInvalidFormula() {
        sheet.set(1, 1, "=2+");
        assertEquals(Ex2Utils.ERR_FORM, sheet.eval(1, 1));
    }

    @Test
    public void testEvalWithCycle() {
        sheet.set(0, 1, "=B2");
        sheet.set(1, 2, "=A1");
        assertEquals(Ex2Utils.ERR_CYCLE, sheet.eval(1, 1));
    }

    @Test
    public void testEvalAll() {
        sheet.set(1, 1, "=2+3");
        sheet.eval();
        assertEquals("5.0", sheet.value(1, 1));
    }

    @Test
    public void testDepth() {
        sheet.set(1, 1, "=B2");
        sheet.set(1, 2, "=3");
        int[][] depths = sheet.depth();
        assertEquals(1, depths[1][1]);
        assertEquals(0, depths[2][2]);
    }

    @Test
    public void testSaveAndLoad() throws IOException {
        sheet.set(1, 1, "123");
        sheet.save("test.csv");

        Ex2Sheet loadedSheet = new Ex2Sheet(5, 5);
        loadedSheet.load("test.csv");
        assertEquals("123", loadedSheet.get(1, 1).getData());
    }

    @Test
    public void testEvaluateFormula() {
        assertEquals("5.0", sheet.evaluateFormula("=2+3"));
        assertEquals(Ex2Utils.ERR_FORM, sheet.evaluateFormula("=2+"));
    }
}