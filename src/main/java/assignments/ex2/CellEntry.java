package assignments.ex2;

public class CellEntry implements Index2D {
    private int x; // Column index (0-based)
    private int y; // Row index (0-based)

    // Constructor to initialize x and y
    public CellEntry(int x, int y) {
        this.x = x;
        this.y = y;
    }

    // Constructor to initialize from a string (e.g., "B3")
    public CellEntry(String cellIndex) {
        if (cellIndex == null || cellIndex.length() < 2) {
            x = -1;
            y = -1;
        } else {
            x = parseColumn(cellIndex);
            y = parseRow(cellIndex);
        }
    }

    // Parse the column part of the cell index (e.g., "B3" -> 1)
    private int parseColumn(String cellIndex) {
        char columnChar = cellIndex.toUpperCase().charAt(0);
        if (columnChar >= 'A' && columnChar <= 'Z') {
            return columnChar - 'A';
        }
        return -1; // Invalid column
    }

    // Parse the row part of the cell index (e.g., "B3" -> 3)
    private int parseRow(String cellIndex) {
        try {
            return Integer.parseInt(cellIndex.substring(1));
        } catch (NumberFormatException e) {
            return -1; // Invalid row
        }
    }

    @Override
    public boolean isValid() {
        // Ensure x and y are within valid ranges
        return x >= 0 && x < 26 && y >= 0 && y < 100;
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
        if (!isValid()) return "Invalid Index";
        char columnChar = (char) ('A' + x);
        return columnChar + Integer.toString(y);
    }
}
