import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
    public void testConstructorWithWidthAndHeight() {
        assertEquals(5, sheet.width());
        assertEquals(5, sheet.height());
    }

    @Test
    public void testDefaultConstructor() {
        Ex2Sheet defaultSheet = new Ex2Sheet();
        assertEquals(Ex2Utils.WIDTH, defaultSheet.width());
        assertEquals(Ex2Utils.HEIGHT, defaultSheet.height());
    }

    @Test
    public void testIsIn() {
        assertTrue(sheet.isIn(0, 0));
        assertTrue(sheet.isIn(4, 4));
        assertFalse(sheet.isIn(5, 5));
        assertFalse(sheet.isIn(-1, -1));
    }

    @Test
    public void testSetAndGet() {
        sheet.set(1, 1, "Test");
        assertEquals("Test", sheet.get(1, 1).getData());
    }

    @Test
    public void testSetAndGetEdgeCases() {
        // Setting and getting data at the boundaries
        sheet.set(0, 0, "BoundaryTest");
        assertEquals("BoundaryTest", sheet.get(0, 0).getData());

        sheet.set(4, 4, "BoundaryTest");
        assertEquals("BoundaryTest", sheet.get(4, 4).getData());

        // Setting and getting data outside the boundaries
        sheet.set(-1, -1, "OutOfBounds");
        assertEquals(null, sheet.get(-1, -1));

        sheet.set(5, 5, "OutOfBounds");
        assertEquals(null, sheet.get(5, 5));
    }

    @Test
    public void testSetAndGetWithEmptyString() {
        sheet.set(2, 2, "");
        assertEquals("", sheet.get(2, 2).getData());
    }

    @Test
    public void testSetAndGetWithNull() {
        sheet.set(2, 2, null);
        assertEquals(Ex2Utils.EMPTY_CELL, sheet.get(2, 2).getData());
    }

    @Test
    public void testGetWithStringCoords() {
        sheet.set(1, 1, "Test");
        assertEquals("Test", sheet.get("B1").getData());
    }

    @Test
    public void testValue() {
        sheet.set(1, 1, "Test");
        assertEquals("Test", sheet.value(1, 1));
        assertEquals(Ex2Utils.EMPTY_CELL, sheet.value(0, 0));
    }

    @Test
    public void testEvaluateFormula() {
        assertEquals("5.0", sheet.evaluateFormula("=2 + 3"));
        assertEquals("1.0", sheet.evaluateFormula("=3 - 2"));
        assertEquals("6.0", sheet.evaluateFormula("=2 * 3"));
        assertEquals("2.0", sheet.evaluateFormula("=6 / 3"));

        assertEquals("5.0", sheet.evaluateFormula("=2 + 3 * 5 / 5"));
        assertEquals("1.0", sheet.evaluateFormula("=10 / 2 - 4"));

        sheet.set(0, 0, "2");
        sheet.set(1, 0, "3");
        assertEquals("5.0", sheet.evaluateFormula("=A0 + B0"));
        assertEquals("6.0", sheet.evaluateFormula("=A0 * B0"));

        assertEquals(Ex2Utils.ERR_FORM, sheet.evaluateFormula("=2 +"));
        assertEquals(Ex2Utils.ERR_FORM, sheet.evaluateFormula("=A1 +"));
        assertEquals(Ex2Utils.ERR_FORM, sheet.evaluateFormula("=A1 + B2"));
        assertEquals(Ex2Utils.ERR_FORM, sheet.evaluateFormula("=2 + 3 *"));
    }

    @Test
    public void testEvaluateFormulaEdgeCases() {
        // Invalid formulas
        assertEquals(Ex2Utils.ERR_FORM, sheet.evaluateFormula("=2 +"));
        assertEquals(Ex2Utils.ERR_FORM, sheet.evaluateFormula("=A1 +"));
        assertEquals(Ex2Utils.ERR_FORM, sheet.evaluateFormula("=A1 + B2"));
        assertEquals(Ex2Utils.ERR_FORM, sheet.evaluateFormula("=2 + 3 *"));

    }

    @Test
    public void testEvalEntireSheet() {
        sheet.set(1, 1, "123");
        sheet.eval();
        assertEquals("123", sheet.eval(1, 1));
    }

    @Test
    public void testDepth() {
        int[][] depths = sheet.depth();
        assertNotNull(depths);
        assertEquals(5, depths.length);
        assertEquals(5, depths[0].length);
    }

    @Test
    public void testDepthEdgeCases() {
        // Depth with no dependencies
        int[][] depths = sheet.depth();
        for (int x = 0; x < sheet.width(); x++) {
            for (int y = 0; y < sheet.height(); y++) {
                assertEquals(0, depths[x][y]);
            }
        }

        // Depth with circular dependencies
        sheet.set(0, 0, "=A0");
        depths = sheet.depth();
        assertEquals(Ex2Utils.ERR, depths[0][0]);

    }

    @Test
    public void testSaveAndLoad() throws IOException {
        sheet.set(1, 1, "Test");
        Path tempFile = Files.createTempFile("sheet", ".txt");
        sheet.save(tempFile.toString());

        Ex2Sheet loadedSheet = new Ex2Sheet(5, 5);
        loadedSheet.load(tempFile.toString());
        assertEquals("Test", loadedSheet.get(1, 1).getData());

        Files.delete(tempFile);
    }

    @Test
    public void testSaveAndLoadEdgeCases() throws IOException {
        // Save and load with empty cells
        Path tempFile = Files.createTempFile("empty_sheet", ".txt");
        sheet.save(tempFile.toString());

        Ex2Sheet loadedSheet = new Ex2Sheet(5, 5);
        loadedSheet.load(tempFile.toString());
        assertEquals(Ex2Utils.EMPTY_CELL, loadedSheet.get(1, 1).getData());

        Files.delete(tempFile);

        // Save and load with special characters
        sheet.set(1, 1, "SpecialChars!@#$%^&*()");
        tempFile = Files.createTempFile("special_chars_sheet", ".txt");
        sheet.save(tempFile.toString());

        loadedSheet = new Ex2Sheet(5, 5);
        loadedSheet.load(tempFile.toString());
        assertEquals("SpecialChars!@#$%^&*()", loadedSheet.get(1, 1).getData());

        Files.delete(tempFile);
    }

    @Test
    public void testClearCell() {
        sheet.set(2, 2, "Test");
        assertEquals("Test", sheet.get(2, 2).getData());

        sheet.set(2, 2, Ex2Utils.EMPTY_CELL);

        assertEquals(Ex2Utils.EMPTY_CELL, sheet.get(2, 2).getData());
    }

    @Test
    public void testDependencyDepth() {
        sheet.set(0, 0, "=B0 + 2");
        sheet.set(1, 0, "=C0 + 1");
        sheet.set(2, 0, "5");

        int[][] depths = sheet.depth();

        assertEquals(2, depths[0][0]);
        assertEquals(1, depths[1][0]);
        assertEquals(0, depths[2][0]);
    }

    @Test
    public void testCircularDependencyDetection() {
        sheet.set(0, 0, "=A0");
        sheet.depth();
        assertEquals(Ex2Utils.ERR_CYCLE, sheet.eval(0, 0));
        sheet.set(0, 0, "=B0");
        sheet.set(1, 0, "=A0");
        sheet.depth();

        assertEquals(Ex2Utils.ERR_CYCLE, sheet.eval(0, 0));

    }

    @Test
    public void testEvalWithInvalidCellReference() {
        sheet.set(0, 0, "=Z9 + 2");
        assertEquals(Ex2Utils.ERR_FORM, sheet.eval(0, 0));
    }

    @Test
    public void testEvalWithMixedReferences() {
        sheet.set(0, 0, "5");
        sheet.set(1, 0, "=A0 * 2");
        assertEquals("10.0", sheet.eval(1, 0));
    }

    @Test
    public void testNegativeNumbersAndReferences() {
        // Test direct negative numbers
        sheet.set(0, 0, "-42");
        assertEquals("-42", sheet.value(0, 0));

        // Test negative cell references
        sheet.set(0, 0, "10");
        sheet.set(1, 0, "=-A0");
        assertEquals("-10.0", sheet.eval(1, 0));

        // Test complex formulas with negatives
        sheet.set(2, 0, "=-A0 + 5");
        assertEquals("-5.0", sheet.eval(2, 0));

        // Test multiple negatives
        sheet.set(3, 0, "=-(-A0)");
        assertEquals("10.0", sheet.eval(3, 0));
    }
}
