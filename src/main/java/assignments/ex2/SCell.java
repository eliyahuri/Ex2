// Simplified Cell Implementation
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

    /**
     * Constructor to initialize a cell with given data.
     *
     * @param data the initial data to store in the cell.
     */
    public SCell(String data) {
        setData(data);
    }

    /**
     * Retrieves the data stored in the cell.
     *
     * @return the data as a String.
     */
    @Override
    public String getData() {
        return data;
    }

    /**
     * Updates the cell's data and determines its type (text, number, or formula).
     *
     * @param data the new data to store in the cell.
     */
    @Override
    public void setData(String data) {
        this.data = data;
        if (data.startsWith("=")) {
            setType(Ex2Utils.FORM);
        } else if (isNumber(data)) {
            setType(Ex2Utils.NUMBER);
        } else {
            setType(Ex2Utils.TEXT);
        }
    }

    /**
     * Checks if the provided data is a valid number.
     *
     * @param data the data to check.
     * @return true if the data is a valid number; false otherwise.
     */
    public boolean isNumber(String data) {
        try {
            Double.valueOf(data);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Retrieves the type of the cell.
     *
     * @return the cell type (text, number, or formula).
     */
    @Override
    public int getType() {
        return type;
    }

    /**
     * Sets the type of the cell explicitly.
     *
     * @param type the new cell type.
     */
    @Override
    public void setType(int type) {
        this.type = type;
    }

    /**
     * Retrieves the evaluation order of the cell.
     *
     * @return the order as an integer.
     */
    @Override
    public int getOrder() {
        return order;
    }

    /**
     * Sets the evaluation order of the cell.
     *
     * @param order the new evaluation order.
     */
    @Override
    public void setOrder(int order) {
        this.order = order;
    }
}
