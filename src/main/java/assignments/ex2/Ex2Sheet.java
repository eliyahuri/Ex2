package assignments.ex2;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
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
        return get(x, y).getData() != null ? eval(x, y) : Ex2Utils.EMPTY_CELL;
    }

    @Override
    public String eval(int x, int y) {
        Cell cell = get(x, y);
        if (cell == null) {
            return Ex2Utils.EMPTY_CELL;
        }
        if (cell.getType() == Ex2Utils.FORM) {
            return evaluateFormula(cell.getData());
        }
        return cell.getData();
    }

    public String evaluateFormula(String formula) {
        if (formula == null || formula.isEmpty() || !formula.startsWith("=")) {
            return Ex2Utils.ERR_FORM;
        }

        formula = formula.substring(1); // Remove the '=' at the beginning

        try {
            // Convert the formula into postfix notation (Reverse Polish Notation) for easier evaluation
            List<String> postfix = infixToPostfix(formula);
            // Evaluate the postfix expression
            return String.valueOf(evaluatePostfix(postfix));
        } catch (Exception e) {
            return Ex2Utils.ERR_FORM;
        }
    }

    private List<String> infixToPostfix(String formula) {
        List<String> postfix = new ArrayList<>();
        Stack<String> operators = new Stack<>();
        Pattern pattern = Pattern.compile("\\d+\\.?\\d*|[a-zA-Z]+\\d+|[()+\\-*/]");
        Matcher matcher = pattern.matcher(formula);

        while (matcher.find()) {
            String token = matcher.group();

            if (isNumber(token) || isCellReference(token)) {
                postfix.add(token);
            } else if ("(".equals(token)) {
                operators.push(token);
            } else if (")".equals(token)) {
                while (!operators.isEmpty() && !"(".equals(operators.peek())) {
                    postfix.add(operators.pop());
                }
                if (operators.isEmpty() || !"(".equals(operators.pop())) {
                    throw new IllegalArgumentException("Mismatched parentheses");
                }
            } else if (isOperator(token)) {
                while (!operators.isEmpty() && precedence(operators.peek()) >= precedence(token)) {
                    postfix.add(operators.pop());
                }
                operators.push(token);
            }
        }

        while (!operators.isEmpty()) {
            String operator = operators.pop();
            if ("(".equals(operator)) {
                throw new IllegalArgumentException("Mismatched parentheses");
            }
            postfix.add(operator);
        }

        return postfix;
    }

    private double evaluatePostfix(List<String> postfix) {
        Stack<Double> values = new Stack<>();

        for (String token : postfix) {
            if (isNumber(token)) {
                values.push(Double.parseDouble(token));
            } else if (isCellReference(token)) {
                Cell cell = get(token);
                if (cell != null && cell.getType() == Ex2Utils.NUMBER) {
                    values.push(Double.parseDouble(cell.getData()));
                } else {
                    throw new IllegalArgumentException("Invalid cell reference or non-numeric cell");
                }
            } else if (isOperator(token)) {
                double b = values.pop();
                double a = values.pop();
                values.push(applyOperator(a, b, token));
            }
        }

        if (values.size() != 1) {
            throw new IllegalArgumentException("Invalid postfix expression");
        }

        return values.pop();
    }

    private boolean isNumber(String token) {
        return token.matches("\\d+\\.?\\d*");
    }

    private boolean isCellReference(String token) {
        return token.matches("[a-zA-Z]+\\d+");
    }

    private boolean isOperator(String token) {
        return "+-*/".contains(token);
    }

    private int precedence(String operator) {
        return switch (operator) {
            case "+", "-" -> 1;
            case "*", "/" -> 2;
            default -> -1;
        };
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
