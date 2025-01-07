package assignments.ex2;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Ex2Sheet implements Sheet {
    private Cell[][] table;

    /**
     * Constructor to initialize a spreadsheet with specified width and height.
     *
     * @param width  the number of columns in the spreadsheet.
     * @param height the number of rows in the spreadsheet.
     */
    public Ex2Sheet(int width, int height) {
        table = new SCell[width][height];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                table[x][y] = new SCell(Ex2Utils.EMPTY_CELL);
            }
        }
    }

    /**
     * Default constructor that initializes the spreadsheet with predefined
     * dimensions.
     */
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
        return isIn(x, y) ? table[x][y] : null;
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
            case Ex2Utils.FORM -> evaluateFormula(data);
            case Ex2Utils.NUMBER -> data;
            case Ex2Utils.TEXT -> data;
            default -> Ex2Utils.ERR_FORM;
        };
    }

    public String evaluateFormula(String formula) {
        if (formula == null || formula.isEmpty() || !formula.startsWith("=")) {
            return Ex2Utils.ERR_FORM;
        }

        formula = formula.substring(1); // Remove the '=' at the beginning

        try {
            // Use a regex to split numbers and operators into tokens
            Pattern pattern = Pattern.compile("\\d+\\.?\\d*|[+\\-*/]");
            Matcher matcher = pattern.matcher(formula);
            List<String> tokens = new ArrayList<>();

            while (matcher.find()) {
                tokens.add(matcher.group());
            }

            if (tokens.size() % 2 == 0) {
                // Formula must have an odd number of tokens (e.g., "1 + 2")
                return Ex2Utils.ERR_FORM;
            }

            // First pass: handle multiplication and division
            double[] values = new double[tokens.size()];
            String[] operators = new String[tokens.size()];
            int valueIndex = 0;
            int operatorIndex = 0;

            double currentValue = getValue(tokens.get(0));
            for (int i = 1; i < tokens.size(); i += 2) {
                String operator = tokens.get(i);
                double nextValue = getValue(tokens.get(i + 1));

                if (operator.equals("*") || operator.equals("/")) {
                    if (operator.equals("/") && nextValue == 0) {
                        return Ex2Utils.ERR_FORM; // Division by zero
                    }
                    currentValue = applyOperator(currentValue, nextValue, operator);
                } else if (operator.equals("+") || operator.equals("-")) {
                    values[valueIndex++] = currentValue;
                    operators[operatorIndex++] = operator;
                    currentValue = nextValue;
                } else {
                    return Ex2Utils.ERR_FORM; // Invalid operator
                }
            }
            values[valueIndex++] = currentValue;

            // Second pass: handle addition and subtraction
            double result = values[0];
            for (int i = 0; i < operatorIndex; i++) {
                result = applyOperator(result, values[i + 1], operators[i]);
            }

            return String.valueOf(result);
        } catch (Exception e) {
            return Ex2Utils.ERR_FORM;
        }
    }

    private double getValue(String token) {
        if (Character.isLetter(token.charAt(0))) {
            // It's a cell reference
            Cell cell = get(token);
            if (cell != null && cell.getType() == Ex2Utils.NUMBER) {
                return Double.parseDouble(cell.getData());
            } else {
                throw new IllegalArgumentException("Invalid cell reference or non-numeric cell");
            }
        } else {
            // It's a number
            return Double.parseDouble(token);
        }
    }

    private double applyOperator(double a, double b, String operator) {
        return switch (operator) {
            case "+" -> a + b;
            case "-" -> a - b;
            case "*" -> a * b;
            case "/" -> a / b;
            default -> throw new IllegalArgumentException("Invalid operator");
        };
    }

    @Override
    public void eval() {
        for (int x = 0; x < width(); x++) {
            for (int y = 0; y < height(); y++) {
                String evaluatedValue = eval(x, y);
                table[x][y] = new SCell(evaluatedValue);
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
        // Placeholder for dependency depth calculation.
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
            throw e;
        }
    }

    @Override
    public void load(String fileName) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            // Clear the existing table
            table = new SCell[width()][height()];
            for (int x = 0; x < width(); x++) {
                for (int y = 0; y < height(); y++) {
                    table[x][y] = new SCell(Ex2Utils.EMPTY_CELL);
                }
            }

            // Validate the file header
            String header = reader.readLine();
            if (header == null || !header.equals("I2CS ArielU: SpreadSheet (Ex2) assignment")) {
                throw new IOException("Invalid file format: Missing or incorrect header.");
            }

            // Read and parse each line
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length != 3) {
                    throw new IOException("Invalid file format: Incorrect number of columns in line: " + line);
                }

                try {
                    int x = Integer.parseInt(parts[0].trim());
                    int y = Integer.parseInt(parts[1].trim());
                    String data = parts[2].trim();

                    if (isIn(x, y)) {
                        table[x][y] = new SCell(data);
                    } else {
                        throw new IOException("Invalid cell coordinates in line: " + line);
                    }
                } catch (NumberFormatException e) {
                    throw new IOException("Invalid cell coordinates format in line: " + line, e);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
    }
}
