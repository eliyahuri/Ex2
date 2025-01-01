package assignments.ex2;

import java.util.regex.Pattern;

public class SCell implements Cell {
    private String line; // Stores the content of the cell
    private int type; // Stores the type of the cell
    private int order; // Stores the computation depth/order of the cell

    public SCell(String s) {
        setData(s);
    }

    @Override
    public String getData() {
        return line;
    }

    @Override
    public void setData(String s) {
        this.line = s;
        // Determine the type of the cell based on its content
        if (isForm(s)) {
            type = Ex2Utils.FORM;
        } else if (isNumber(s)) {
            type = Ex2Utils.NUMBER;
        } else if (isText(s)) {
            type = Ex2Utils.TEXT;
        } else {
            type = Ex2Utils.ERR;
        }
    }

    @Override
    public int getType() {
        return type;
    }

    @Override
    public void setType(int t) {
        this.type = t;
    }

    @Override
    public int getOrder() {
        return order;
    }

    @Override
    public void setOrder(int o) {
        this.order = o;
    }

    // Utility to check if the content is a number
    public static boolean isNumber(String text) {
        try {
            Double.valueOf(text);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // Utility to check if the content is plain text
    public static boolean isText(String text) {
        return !isNumber(text) && !isForm(text);
    }

    // Utility to check if the content is a formula
    public static boolean isForm(String text) {
        if (text.startsWith("=")) {
            String formula = text.substring(1);
            return isValidForm(formula);
        }
        return false;
    }

    // Validate a formula string
    public static boolean isValidForm(String formula) {

        // A simple regex to match basic formulas
        String validFormulaRegex = "^[-+*/0-9A-Za-z().]+$";
        if (!Pattern.matches(validFormulaRegex, formula)) {
            return false;
        }

        // Check for balanced parentheses
        int balance = 0;
        for (char c : formula.toCharArray()) {
            if (c == '(')
                balance++;
            if (c == ')')
                balance--;
            if (balance < 0)
                return false; // More closing than opening
        }
        return balance == 0;
    }

    @Override
    public String toString() {
        return getData();
    }
}
