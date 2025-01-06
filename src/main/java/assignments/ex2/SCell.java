// Simplified Cell Implementation
package assignments.ex2;

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
            this.type = Ex2Utils.FORM;
        } else if (isNumber(data)) {
            this.type = Ex2Utils.NUMBER;
        } else {
            this.type = Ex2Utils.TEXT;
        }
    }

    private boolean isNumber(String data) {
        try {
            Double.parseDouble(data);
            return true;
        } catch (NumberFormatException e) {
            return false;
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
}

