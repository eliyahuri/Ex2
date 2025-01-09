package assignments.ex2;

public class CellEntry implements Index2D {
    private final int x;
    private final int y;

    /**
     * Constructs a CellEntry with the specified x and y coordinates.
     *
     * @param x the x-coordinate of the cell
     * @param y the y-coordinate of the cell
     */
    public CellEntry(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Constructs a CellEntry from a string representation of the cell index.
     *
     * @param cellIndex the string representation of the cell index (e.g., "A1")
     */
    public CellEntry(String cellIndex) {
        if (cellIndex == null || cellIndex.isEmpty()) {
            this.x = -1;
            this.y = -1;
            return;
        }
        int i = 0;
        while (i < cellIndex.length() && Character.isLetter(cellIndex.charAt(i))) {
            i++;
        }
        String colPart = cellIndex.substring(0, i);
        String rowPart = cellIndex.substring(i);
        this.x = parseColumn(colPart);
        this.y = parseRow(rowPart);
    }

    /**
     * Parses the column part of the cell index.
     *
     * @param cellIndex the column part of the cell index
     * @return the parsed column index as an integer
     */
    private int parseColumn(String cellIndex) {
        char columnChar = Character.toUpperCase(cellIndex.charAt(0));
        return (columnChar >= 'A' && columnChar <= 'Z') ? columnChar - 'A' : -1;
    }

    /**
     * Parses the row part of the cell index.
     *
     * @param cellIndex the row part of the cell index
     * @return the parsed row index as an integer
     */
    private int parseRow(String cellIndex) {
        try {
            return Integer.parseInt(cellIndex);
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
        return (char) ('A' + x) + Integer.toString(y);
    }
}