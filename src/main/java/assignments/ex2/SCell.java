package assignments.ex2;

/**
 * SCell represents a single cell in the spreadsheet, capable of storing data
 * in various formats such as text, numbers, or formulas. The cell determines
 * its type based on the provided data.
 */
public class SCell implements Cell {
    private String data;
    private int type;
    private int order;

    public SCell(String data) {
        setData(data);
    }

    @Override
    public String getData() {
        return data;
    }

    /**
     * Checks if the provided data is a valid formula.
     *
     * @param data the data to check
     * @return true if the data is a valid formula, false otherwise
     */
    public boolean isForm(String data) {
        if (!data.startsWith("=") || data.length() < 2) {
            return false;
        }

        String formula = data.substring(1).trim();
        String[] tokens = formula.split("\\s+");
        boolean expectOperand = true;

        for (String token : tokens) {
            if (token.isEmpty()) {
                continue;
            }

            if (token.matches("[A-Za-z]\\d+")) { // Cell reference like A1, B2
                if (!expectOperand) {
                    return false;
                }
                expectOperand = false;
            } else if (token.matches("-?\\d+\\.?\\d*")) { // Number
                if (!expectOperand) {
                    return false;
                }
                expectOperand = false;
            } else if ("+-*/".contains(token)) { // Operator
                if (expectOperand) {
                    return false;
                }
                expectOperand = true;
            } else {
                return false; // Invalid token
            }
        }

        return !expectOperand; // Must end with an operand
    }

    @Override
    public void setData(String data) {
        if (data == null) {
            data = Ex2Utils.EMPTY_CELL;
        }
        this.data = data;
        if (data.startsWith("=")) {
            if (isForm(data)) {
                setType(Ex2Utils.FORM);
            } else {
                setType(Ex2Utils.ERR_FORM_FORMAT);
            }
        } else if (isNumber(data)) {
            setType(Ex2Utils.NUMBER);
        } else {
            setType(Ex2Utils.TEXT);
        }
    }

    @Override
    public int getType() {
        return type;
    }

    @Override
    public void setType(int type) {
        this.type = type;
    }

    @Override
    public int getOrder() {
        return order;
    }

    @Override
    public void setOrder(int order) {
        this.order = order;
    }

    /**
     * Checks if the provided data is a number.
     *
     * @param data the data to check
     * @return true if the data is a number, false otherwise
     */
    public boolean isNumber(String data) {
        try {
            Double.valueOf(data);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}