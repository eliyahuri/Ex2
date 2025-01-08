package assignments.ex2;

public class CellEntry implements Index2D {
    private final int x;
    private final int y;

    public CellEntry(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public CellEntry(String cellIndex) {
        if (cellIndex == null || cellIndex.isEmpty()) {
            this.x = -1;
            this.y = -1;
            return;
        }
        int i = 0;
        // Collect all letters for the column
        while (i < cellIndex.length() && Character.isLetter(cellIndex.charAt(i))) {
            i++;
        }
        String colPart = cellIndex.substring(0, i);
        String rowPart = cellIndex.substring(i);
        this.x = parseColumn(colPart);
        this.y = parseRow(rowPart);
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
        System.out.println((String) ((char) ('A' + x) + Integer.toString(y)));
        if (!isValid()) {
            return "Invalid Index";
        }
        return (String) ((char) ('A' + x) + Integer.toString(y));
    }
}
