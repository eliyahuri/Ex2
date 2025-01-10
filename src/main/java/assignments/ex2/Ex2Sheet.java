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

/**
 * Implementation of a simple spreadsheet that supports basic operations like
 * setting, retrieving, and evaluating cell values. Includes formula parsing and
 * evaluation.
 */
public class Ex2Sheet implements Sheet {
    private Cell[][] table;

    // Constructors
    /**
     * Constructs a new Ex2Sheet with the specified width and height.
     * 
     * @param width  the width of the spreadsheet
     * @param height the height of the spreadsheet
     */
    public Ex2Sheet(int width, int height) {
        table = new SCell[width][height];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                table[x][y] = new SCell(Ex2Utils.EMPTY_CELL); // Initialize each cell with an empty value
            }
        }
    }

    /**
     * Constructs a new Ex2Sheet with default width and height.
     */
    public Ex2Sheet() {
        this(Ex2Utils.WIDTH, Ex2Utils.HEIGHT);
    }

    // Basic operations
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
            if (data == null) {
                data = Ex2Utils.EMPTY_CELL;
            }
            table[x][y] = new SCell(data); // Set the data in the specified cell
        }
    }

    @Override
    public Cell get(int x, int y) {
        return isIn(x, y) ? table[x][y] : null; // Return the cell or null if out of bounds
    }

    @Override
    public Cell get(String coords) {
        CellEntry entry = new CellEntry(coords); // Parse coordinates like "A1" into indices
        if (entry.isValid() && isIn(entry.getX(), entry.getY())) {
            return get(entry.getX(), entry.getY());
        }
        return null;
    }

    @Override
    public String value(int x, int y) {
        return get(x, y).getData() != null ? eval(x, y) : Ex2Utils.EMPTY_CELL; // Evaluate cell value if non-empty
    }

    @Override
    public String eval(int x, int y) {
        Cell cell = get(x, y);
        if (cell == null) {
            return Ex2Utils.EMPTY_CELL;
        }
        if (cell.getType() == Ex2Utils.ERR_CYCLE_FORM) {
            return Ex2Utils.ERR_CYCLE; // Correctly handle cycle error
        }
        if (cell.getType() == Ex2Utils.FORM) {
            return evaluateFormula(cell.getData());
        }
        return cell.getData();
    }

    @Override
    public void eval() {
        for (int x = 0; x < width(); x++) {
            for (int y = 0; y < height(); y++) {
                eval(x, y); // Evaluate all cells
            }
        }
    }

    // Formula evaluation
    /**
     * Evaluates a formula and returns the result as a string.
     * 
     * @param formula the formula to evaluate
     * @return the result of the formula evaluation
     */
    public String evaluateFormula(String formula) {
        if (formula == null || formula.isEmpty() || !formula.startsWith("=")) {
            return Ex2Utils.ERR_FORM; // Return an error if not a valid formula
        }

        formula = formula.substring(1); // Remove the '=' at the beginning

        try {
            // Convert formula to postfix notation (Reverse Polish Notation)
            List<String> postfix = infixToPostfix(formula);
            // Evaluate the postfix expression
            return String.valueOf(evaluatePostfix(postfix));
        } catch (Exception e) {
            return Ex2Utils.ERR_FORM;
        }
    }

    /**
     * Converts an infix formula to postfix notation (Reverse Polish Notation).
     * 
     * @param formula the infix formula
     * @return the postfix notation as a list of strings
     */
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

    /**
     * Evaluates a postfix expression and returns the result.
     * 
     * @param postfix the postfix expression as a list of strings
     * @return the result of the evaluation
     */
    private double evaluatePostfix(List<String> postfix) {
        Stack<Double> values = new Stack<>();

        for (String token : postfix) {
            if (isNumber(token)) {
                values.push(Double.valueOf(token));
            } else if (isCellReference(token)) {
                Cell cell = get(token);
                if (cell != null) {
                    if (cell.getType() == Ex2Utils.NUMBER) {
                        values.push(Double.valueOf(cell.getData()));
                    } else if (cell.getType() == Ex2Utils.FORM) {
                        String evaluatedValue = evaluateFormula(cell.getData());
                        if (evaluatedValue.equals(Ex2Utils.ERR_FORM)) {
                            throw new IllegalArgumentException("Invalid formula in cell reference");
                        }
                        values.push(Double.valueOf(evaluatedValue));
                    } else {
                        throw new IllegalArgumentException("Invalid cell reference or non-numeric cell");
                    }
                } else {
                    throw new IllegalArgumentException("Invalid cell reference");
                }
            } else if (isOperator(token)) {
                if (values.isEmpty()) {
                    throw new IllegalArgumentException("Invalid postfix expression");
                }
                double b = values.pop();
                if (values.isEmpty()) {
                    throw new IllegalArgumentException("Invalid postfix expression");
                }
                double a = values.pop();
                values.push(applyOperator(a, b, token));
            }
        }

        if (values.size() != 1) {
            throw new IllegalArgumentException("Invalid postfix expression");
        }

        return values.pop();
    }

    /**
     * Checks if a token is a number.
     * 
     * @param token the token to check
     * @return true if the token is a number, false otherwise
     */
    private boolean isNumber(String token) {
        return token.matches("\\d+\\.?\\d*");
    }

    /**
     * Checks if a token is a cell reference.
     * 
     * @param token the token to check
     * @return true if the token is a cell reference, false otherwise
     */
    private boolean isCellReference(String token) {
        return token.matches("[a-zA-Z]\\d+");
    }

    /**
     * Checks if a token is an operator.
     * 
     * @param token the token to check
     * @return true if the token is an operator, false otherwise
     */
    private boolean isOperator(String token) {
        return "+-*/".contains(token);
    }

    /**
     * Returns the precedence of an operator.
     * 
     * @param operator the operator
     * @return the precedence of the operator
     */
    private int precedence(String operator) {
        return switch (operator) {
            case "+", "-" -> 1;
            case "*", "/" -> 2;
            default -> -1;
        };
    }

    /**
     * Applies an operator to two operands and returns the result.
     * 
     * @param a        the first operand
     * @param b        the second operand
     * @param operator the operator
     * @return the result of applying the operator
     */
    private double applyOperator(double a, double b, String operator) {
        return switch (operator) {
            case "+" -> a + b;
            case "-" -> a - b;
            case "*" -> a * b;
            case "/" -> a / b;
            default -> throw new IllegalArgumentException("Invalid operator");
        };
    }

    // Depth calculation
    @Override
    public int[][] depth() {
        int[][] depths = new int[width()][height()];
        for (int x = 0; x < width(); x++) {
            for (int y = 0; y < height(); y++) {
                depths[x][y] = computeDepth(x, y, new boolean[width()][height()]); // Updated depth calculation
            }
        }
        return depths;
    }

    /**
     * Computes the depth of a cell.
     * 
     * @param x       the x-coordinate of the cell
     * @param y       the y-coordinate of the cell
     * @param visited a boolean array to track visited cells
     * @return the depth of the cell
     */
    private int computeDepth(int x, int y, boolean[][] visited) {
        if (!isIn(x, y)) {
            return Ex2Utils.ERR;
        }
        if (visited[x][y]) {
            get(x, y).setType(Ex2Utils.ERR_CYCLE_FORM);
            return Ex2Utils.ERR;
        }
        visited[x][y] = true;
        Cell cell = get(x, y);
        if (cell.getType() != Ex2Utils.FORM) {
            return 0;
        }
        String formula = cell.getData().substring(1); // Remove '='
        List<String> tokens = infixToPostfix(formula);
        int maxDepth = 0;
        for (String token : tokens) {
            if (isCellReference(token)) {
                CellEntry entry = new CellEntry(token);
                int depth = computeDepth(entry.getX(), entry.getY(), visited);
                if (depth == Ex2Utils.ERR) {
                    return Ex2Utils.ERR;
                }
                maxDepth = Math.max(maxDepth, depth);
            } else if (isNumber(token)) {
                // Handle numeric values
                int depth = 0; // Numeric values have a depth of 0
                maxDepth = Math.max(maxDepth, depth);
            }
        }
        visited[x][y] = false;
        return 1 + maxDepth;
    }

    // File operations
    @Override
    public void save(String fileName) throws IOException {
        try (Writer writer = new FileWriter(fileName)) {
            writer.write("I2CS ArielU: SpreadSheet (Ex2) assignment\n"); // Write header
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