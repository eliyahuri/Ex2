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
    public void testEval() {
        sheet.set(1, 1, "123");
        assertEquals("123", sheet.eval(1, 1));
        sheet.set(1, 1, "=A1+B1");
        assertEquals("=A1+B1", sheet.eval(1, 1)); // Placeholder for formula evaluation
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
    public void testSaveAndLoad() throws IOException {
        sheet.set(1, 1, "Test");
        Path tempFile = Files.createTempFile("sheet", ".txt");
        sheet.save(tempFile.toString());

        Ex2Sheet loadedSheet = new Ex2Sheet(5, 5);
        loadedSheet.load(tempFile.toString());
        assertEquals("Test", loadedSheet.get(1, 1).getData());

        Files.delete(tempFile);
    }
}