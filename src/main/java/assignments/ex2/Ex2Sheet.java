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
 * A simplified implementation of a spreadsheet that supports basic operations
 * like setting, retrieving, and evaluating cell values, including formula
 * parsing
 * and evaluation.
 */
public class Ex2Sheet implements Sheet {
    private Cell[][] table;

    // Constructors

    /**
     * Constructs a new Ex2Sheet with the specified width and height.
     *
     * @param width  the number of columns
     * @param height the number of rows
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
     * Constructs a new Ex2Sheet with default dimensions.
     */
    public Ex2Sheet() {
        this(Ex2Utils.WIDTH, Ex2Utils.HEIGHT);
    }

    // Overridden Methods from Sheet Interface

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
            table[x][y].setData(data != null ? data : Ex2Utils.EMPTY_CELL);
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
        if (cell == null || cell.getData().isEmpty()) {
            return Ex2Utils.EMPTY_CELL;
        }
        return eval(x, y);
    }

    @Override
    public String eval(int x, int y) {
        Cell cell = get(x, y);
        if (cell == null) {
            return Ex2Utils.EMPTY_CELL;
        }
        if (cell.getType() == Ex2Utils.ERR_FORM_FORMAT) {
            return Ex2Utils.ERR_FORM;
        }
        if (cell.getType() == Ex2Utils.ERR_CYCLE_FORM) {
            return Ex2Utils.ERR_CYCLE;
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
                eval(x, y);
            }
        }
    }

    @Override
    public int[][] depth() {
        int[][] depths = new int[width()][height()];
        boolean[][] visited = new boolean[width()][height()];
        for (int x = 0; x < width(); x++) {
            for (int y = 0; y < height(); y++) {
                depths[x][y] = computeDepth(x, y, visited);
            }
        }
        return depths;
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
        }
    }

    @Override
    public void load(String fileName) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            // Initialize all cells to empty
            for (int x = 0; x < width(); x++) {
                for (int y = 0; y < height(); y++) {
                    table[x][y].setData(Ex2Utils.EMPTY_CELL);
                }
            }

            // Validate header
            String header = reader.readLine();
            if (header == null || !header.equals("I2CS ArielU: SpreadSheet (Ex2) assignment")) {
                throw new IOException("Invalid file format: Incorrect header.");
            }

            // Read cell data
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length != 3) {
                    throw new IOException("Invalid line format: " + line);
                }
                int x = Integer.parseInt(parts[0].trim());
                int y = Integer.parseInt(parts[1].trim());
                String data = parts[2].trim();
                if (isIn(x, y)) {
                    table[x][y].setData(data);
                } else {
                    throw new IOException("Cell coordinates out of bounds: " + line);
                }
            }
        }
    }

    // Private Helper Methods

    /**
     * Evaluates a formula string and returns the result.
     *
     * @param formula the formula to evaluate
     * @return the evaluation result or an error string
     */
    public String evaluateFormula(String formula) {
        if (formula == null || !formula.startsWith("=")) {
            return Ex2Utils.ERR_FORM;
        }

        String expression = formula.substring(1);
        try {
            List<String> postfix = infixToPostfix(expression);
            double result = evaluatePostfix(postfix);
            return String.valueOf(result);
        } catch (Exception e) {
            return Ex2Utils.ERR_FORM;
        }
    }

    /**
     * Converts an infix expression to postfix notation.
     *
     * @param expression the infix expression
     * @return a list of tokens in postfix order
     */
    private List<String> infixToPostfix(String expression) {
        List<String> postfix = new ArrayList<>();
        Stack<String> operators = new Stack<>();
        Pattern pattern = Pattern.compile("-?\\d+\\.?\\d*|[A-Za-z]\\d+|[()+\\-*/]");
        Matcher matcher = pattern.matcher(expression);
        boolean expectOperand = true;

        while (matcher.find()) {
            String token = matcher.group();

            if (token.equals("-") && expectOperand) {
                token = "u-"; // Unary minus
            }

            if (isNumber(token) || isCellReference(token)) {
                postfix.add(token);
                expectOperand = false;
            } else if (token.equals("(")) {
                operators.push(token);
                expectOperand = true;
            } else if (token.equals(")")) {
                while (!operators.isEmpty() && !operators.peek().equals("(")) {
                    postfix.add(operators.pop());
                }
                if (!operators.isEmpty()) {
                    operators.pop(); // Remove '('
                }
                expectOperand = false;
            } else if (isOperator(token)) {
                while (!operators.isEmpty() && precedence(operators.peek()) >= precedence(token)) {
                    postfix.add(operators.pop());
                }
                operators.push(token);
                expectOperand = true;
            }
        }

        while (!operators.isEmpty()) {
            String op = operators.pop();
            if (!op.equals("(")) {
                postfix.add(op);
            }
        }

        return postfix;
    }

    /**
     * Evaluates a postfix expression and returns the numerical result.
     *
     * @param postfix the postfix expression
     * @return the result of evaluation
     */
    private double evaluatePostfix(List<String> postfix) {
        Stack<Double> stack = new Stack<>();

        for (String token : postfix) {
            if (isNumber(token)) {
                stack.push(Double.parseDouble(token));
            } else if (isCellReference(token)) {
                Cell cell = get(token);
                if (cell != null) {
                    String cellVal = eval(new CellEntry(token).getX(), new CellEntry(token).getY());
                    stack.push(Double.parseDouble(cellVal));
                } else {
                    stack.push(0.0);
                }
            } else if (token.equals("u-")) {
                double val = stack.pop();
                stack.push(-val);
            } else if (isOperator(token)) {
                double b = stack.pop();
                double a = stack.pop();
                stack.push(applyOperator(a, b, token));
            }
        }

        return stack.pop();
    }

    /**
     * Computes the depth of a cell based on its dependencies.
     *
     * @param x       the column index
     * @param y       the row index
     * @param visited tracks visited cells to detect cycles
     * @return the depth value or Ex2Utils.ERR in case of a cycle
     */
    private int computeDepth(int x, int y, boolean[][] visited) {
        if (!isIn(x, y)) {
            return Ex2Utils.ERR;
        }
        if (visited[x][y]) {
            get(x, y).setType(Ex2Utils.ERR_CYCLE_FORM);
            return Ex2Utils.ERR;
        }

        Cell cell = get(x, y);
        if (cell.getType() != Ex2Utils.FORM) {
            return 0;
        }

        visited[x][y] = true;
        String formula = cell.getData().substring(1);
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
            }
        }

        visited[x][y] = false;
        return 1 + maxDepth;
    }

    // Utility Methods

    private boolean isNumber(String token) {
        return token.matches("-?\\d+\\.?\\d*");
    }

    private boolean isCellReference(String token) {
        return token.matches("[A-Za-z]\\d+");
    }

    private boolean isOperator(String token) {
        return "+-*/".contains(token) || token.equals("u-");
    }

    private int precedence(String operator) {
        switch (operator) {
            case "u-":
                return 3;
            case "*", "/":
                return 2;
            case "+", "-":
                return 1;
            default:
                return -1;
        }
    }

    private double applyOperator(double a, double b, String operator) {
        return switch (operator) {
            case "+" -> a + b;
            case "-" -> a - b;
            case "*" -> a * b;
            case "/" -> b != 0 ? a / b : Ex2Utils.ERR;
            default -> 0;
        };
    }
}
