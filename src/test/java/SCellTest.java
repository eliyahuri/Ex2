import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import assignments.ex2.Ex2Utils;
import assignments.ex2.SCell;

public class SCellTest {

    @Test
    public void testConstructorAndGetData() {
        SCell cell = new SCell("=A1+B2");
        assertEquals("=A1+B2", cell.getData());
    }

    @Test
    public void testSetDataFormula() {
        SCell cell = new SCell(null);
        cell.setData("=A1+B2");
        assertEquals("=A1+B2", cell.getData());
        assertEquals(Ex2Utils.FORM, cell.getType());
    }

    @Test
    public void testSetDataNumber() {
        SCell cell = new SCell(null);
        cell.setData("123");
        assertEquals("123", cell.getData());
        assertEquals(Ex2Utils.NUMBER, cell.getType());
    }

    @Test
    public void testSetDataText() {
        SCell cell = new SCell(null);
        cell.setData("Hello");
        assertEquals("Hello", cell.getData());
        assertEquals(Ex2Utils.TEXT, cell.getType());
    }

    @Test
    public void testIsFormValid() {
        SCell cell = new SCell(null);
        assertTrue(cell.isForm("=A1+B2"));
        assertTrue(cell.isForm("=1+2"));
        assertTrue(cell.isForm("=(A1+B2)*C3"));
    }

    @Test
    public void testIsFormInvalid() {
        SCell cell = new SCell(null);
        assertFalse(cell.isForm("A1+B2"));
        assertFalse(cell.isForm("=A1+"));
        assertFalse(cell.isForm("=1+2*"));
    }

    @Test
    public void testIsNumberValid() {
        SCell cell = new SCell(null);
        assertTrue(cell.isNumber("123"));
        assertTrue(cell.isNumber("123.45"));
    }

    @Test
    public void testIsNumberInvalid() {
        SCell cell = new SCell(null);
        assertFalse(cell.isNumber("123a"));
        assertFalse(cell.isNumber("abc"));
    }


    @Test
    public void testSetDataEmptyString() {
        SCell cell = new SCell(null);
        cell.setData("");
        assertEquals("", cell.getData());
        assertEquals(Ex2Utils.TEXT, cell.getType());
    }

    @Test
    public void testSetDataNull() {
        SCell cell = new SCell(null);
        cell.setData(null);
        assertEquals(Ex2Utils.EMPTY_CELL, cell.getData());
        assertEquals(Ex2Utils.TEXT, cell.getType());
    }

    @Test
    public void testIsFormWithSpaces() {
        SCell cell = new SCell(null);
        assertTrue(cell.isForm("= A1 + B2"));
        assertTrue(cell.isForm("= ( A1 + B2 ) * C3"));
    }

    @Test
    public void testIsFormWithInvalidCharacters() {
        SCell cell = new SCell(null);
        assertFalse(cell.isForm("=A1+B2$"));
        assertFalse(cell.isForm("=A1+@B2"));
    }

    @Test
    public void testIsNumberWithLeadingZeros() {
        SCell cell = new SCell(null);
        assertTrue(cell.isNumber("00123"));
        assertTrue(cell.isNumber("000.45"));
    }

    @Test
    public void testIsNumberWithMultipleDots() {
        SCell cell = new SCell(null);
        assertFalse(cell.isNumber("123.45.67"));
    }

    @Test
    public void testSetDataSpecialCharacters() {
        SCell cell = new SCell(null);
        cell.setData("!@#$%^&*()");
        assertEquals("!@#$%^&*()", cell.getData());
        assertEquals(Ex2Utils.TEXT, cell.getType());
    }
}