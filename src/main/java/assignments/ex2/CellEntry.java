package assignments.ex2;

public class CellEntry implements Index2D {
    private final int x;
    private final int y;

    public CellEntry(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public CellEntry(String cellIndex) {
        if (cellIndex == null || cellIndex.length() != 2) {
            this.x = -1;
            this.y = -1;
        } else {
            this.x = parseColumn(cellIndex);
            this.y = parseRow(cellIndex);
        }
    }

    private int parseColumn(String cellIndex) {
        char columnChar = Character.toUpperCase(cellIndex.charAt(0));
        return (columnChar >= 'A' && columnChar <= 'Z') ? columnChar - 'A' : -1;
    }

    private int parseRow(String cellIndex) {
        try {
            return Integer.parseInt(cellIndex.substring(1));
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    @Override
    public boolean isValid() {
        return x >= 0 && x < Ex2Utils.WIDTH && y >= 0 && y < Ex2Utils.HEIGHT;
    }

    @Override
    public int getX() {
        return x;
    }

    @Override
    public int getY() {
        return y;
    }

    @Override
    public String toString() {
        if (!isValid()) {
            return "Invalid Index";
        }
        return (String) ((char) ('A' + x) + Integer.toString(y));
    }
}
