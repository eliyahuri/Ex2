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

    public boolean isForm(String value) {
        return value.startsWith("=");
    }

    public boolean isText(String value) {
        return !isNumber(value) && !isForm(value);
    }

}
