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

    public boolean isNumber(String data) {
        try {
            Double.valueOf(data);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}