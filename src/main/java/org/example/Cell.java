package org.example;

public class Cell {
    private String value;

    public Cell(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean isNumber(String value) {
        try {
            Double.valueOf(value);
            return true;
        } catch (NullPointerException e) {
            return false;
        }
    }

    public boolean isFormula(String value) {
       boolean bool= value.startsWith("=");

       return bool;
    }

    public boolean isText(String value) {
        return !isNumber(value) && !isFormula(value);
    }

}
