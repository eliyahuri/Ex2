package assignments.ex2;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;



public class Ex2Sheet implements Sheet {
    private Cell[][] table;

    public Ex2Sheet(int width, int height) {
        table = new SCell[width][height];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                table[x][y] = new SCell(Ex2Utils.EMPTY_CELL);
            }
        }
    }

    public Ex2Sheet() {
        this(Ex2Utils.WIDTH, Ex2Utils.HEIGHT);
    }

    @Override
    public boolean isIn(int x, int y) {
        return x >= 0 && x < table.length && y >= 0 && y < table[0].length;
    }

    @Override
    public int width() {
        return table.length;
    }

    @Override
    public int height() {
        return table[0].length;
    }

    @Override
    public void set(int x, int y, String data) {
        if (isIn(x, y)) {
            table[x][y] = new SCell(data);
        }
    }

    @Override
    public Cell get(int x, int y) {
        if (isIn(x, y)) {
            return table[x][y];
        }
        return null;
    }

    @Override
    public Cell get(String coords) {
        CellEntry entry = new CellEntry(coords);
        if (entry.isValid() && isIn(entry.getX(), entry.getY())) {
            return get(entry.getX(), entry.getY());
        }
        return null;
    }

    @Override
    public String value(int x, int y) {
        Cell cell = get(x, y);
        return cell != null ? cell.getData() : Ex2Utils.EMPTY_CELL;
    }

    @Override
    public String eval(int x, int y) {
        Cell cell = get(x, y);
        if (cell == null) {
            return Ex2Utils.EMPTY_CELL;
        }
        String data = cell.getData();
        return switch (cell.getType()) {
            case Ex2Utils.NUMBER -> data;
            case Ex2Utils.TEXT -> data;
            case Ex2Utils.FORM -> evaluateFormula(data);
            default -> Ex2Utils.ERR_FORM;
        };
    }

    private String evaluateFormula(String formula) {
        // This is where you would parse and evaluate the formula.
        // For simplicity, returning the formula itself.
        return formula;
    }

    @Override
    public void eval() {
        for (int x = 0; x < width(); x++) {
            for (int y = 0; y < height(); y++) {
                eval(x, y);
            }
        }
    }

    @Override
    public int[][] depth() {
        int[][] depths = new int[width()][height()];
        for (int x = 0; x < width(); x++) {
            for (int y = 0; y < height(); y++) {
                depths[x][y] = computeDepth(x, y);
            }
        }
        return depths;
    }

    private int computeDepth(int x, int y) {
        // Simple stub for depth calculation.
        // Replace with actual dependency depth logic.
        return 0;
    }

    @Override
    public void save(String fileName) throws IOException {
        try (Writer writer = new FileWriter(fileName)) {
            writer.write("I2CS ArielU: SpreadSheet (Ex2) assignment\n");
            for (int x = 0; x < width(); x++) {
                for (int y = 0; y < height(); y++) {
                    Cell cell = get(x, y);
                    if (cell != null && !cell.getData().isEmpty()) {
                        writer.write(x + "," + y + "," + cell.getData() + "\n");
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void load(String fileName) {
        // Implement the loading logic using a Reader class or manual parsing.
        // Ensure that old data is cleared and replaced with loaded content.
    }
}
