package assignments.ex2;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SCell implements Cell {
    private String data;
    private int type;
    private int order;

    // Define operator precedence
    private static final Map<String, Integer> OP_PRECEDENCE = Map.of(
            "+", 1,
            "-", 1,
            "*", 2,
            "/", 2);

    public SCell(String data) {
        setData(data);
    }

    @Override
    public String getData() {
        return data;
    }

    /**
     * Checks if the provided data is a valid formula.
     *
     * @param data the data to check
     * @return true if the data is a valid formula, false otherwise
     */
    public boolean isForm(String data) {
        if (data == null || !data.startsWith("=") || data.length() < 2) {
            return false;
        }

        String expression = data.substring(1).trim();
        if (expression.isEmpty()) {
            return false;
        }

        try {
            List<String> tokens = tokenize(expression);
            parseExpression(tokens);
            // After parsing, there should be no remaining tokens
            return tokens.isEmpty();
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    @Override
    public void setData(String data) {
        if (data == null) {
            data = Ex2Utils.EMPTY_CELL;
        }
        this.data = data;
        if (data.startsWith("=")) {
            if (isForm(data)) {
                setType(Ex2Utils.FORM);
            } else {
                setType(Ex2Utils.ERR_FORM_FORMAT);
            }
        } else if (isNumber(data)) {
            setType(Ex2Utils.NUMBER);
        } else {
            setType(Ex2Utils.TEXT);
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

    /**
     * Checks if the provided data is a number.
     *
     * @param data the data to check
     * @return true if the data is a number, false otherwise
     */
    public boolean isNumber(String data) {
        try {
            Double.valueOf(data);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Tokenizes the input expression into a list of tokens.
     *
     * @param expr the expression to tokenize
     * @return a list of tokens
     * @throws IllegalArgumentException if an invalid token is encountered
     */
    private List<String> tokenize(String expr) {
        List<String> tokens = new ArrayList<>();
        int i = 0;
        int length = expr.length();
        while (i < length) {
            char c = expr.charAt(i);

            if (Character.isWhitespace(c)) {
                i++;
                continue;
            }

            // Handle operators and parentheses
            if (c == '(' || c == ')' || OP_PRECEDENCE.containsKey(String.valueOf(c))) {
                // Check for unary minus
                if (c == '-' && (tokens.isEmpty() || isOperator(tokens.get(tokens.size() - 1))
                        || tokens.get(tokens.size() - 1).equals("("))) {
                    // It's a unary minus, attach it to the number
                    StringBuilder sb = new StringBuilder();
                    sb.append(c);
                    i++;
                    // After unary minus, expect a number or cell reference
                    if (i < length) {
                        char next = expr.charAt(i);
                        if (Character.isDigit(next) || next == '.') {
                            while (i < length && (Character.isDigit(expr.charAt(i)) || expr.charAt(i) == '.')) {
                                sb.append(expr.charAt(i));
                                i++;
                            }
                            tokens.add(sb.toString());
                            continue;
                        } else if (Character.isLetter(next)) {
                            while (i < length && Character.isLetterOrDigit(expr.charAt(i))) {
                                sb.append(expr.charAt(i));
                                i++;
                            }
                            tokens.add(sb.toString());
                            continue;
                        } else {
                            throw new IllegalArgumentException("Invalid token after unary minus");
                        }
                    } else {
                        throw new IllegalArgumentException("Expression cannot end with unary minus");
                    }
                } else {
                    tokens.add(String.valueOf(c));
                    i++;
                }
                continue;
            }

            // Handle numbers
            if (Character.isDigit(c) || c == '.') {
                StringBuilder sb = new StringBuilder();
                boolean hasDot = false;
                while (i < length && (Character.isDigit(expr.charAt(i)) || expr.charAt(i) == '.')) {
                    if (expr.charAt(i) == '.') {
                        if (hasDot) {
                            throw new IllegalArgumentException("Multiple decimal points in number");
                        }
                        hasDot = true;
                    }
                    sb.append(expr.charAt(i));
                    i++;
                }
                tokens.add(sb.toString());
                continue;
            }

            // Handle cell references
            if (Character.isLetter(c)) {
                StringBuilder sb = new StringBuilder();
                sb.append(c);
                i++;
                while (i < length && Character.isLetterOrDigit(expr.charAt(i))) {
                    sb.append(expr.charAt(i));
                    i++;
                }
                tokens.add(sb.toString());
                continue;
            }

            throw new IllegalArgumentException("Invalid character encountered: " + c);
        }
        return tokens;
    }

    /**
     * Parses the expression using recursive descent without using a separate Parser
     * class.
     *
     * @param tokens the list of tokens to parse
     * @throws IllegalArgumentException if a parsing error occurs
     */
    private void parseExpression(List<String> tokens) {
        parseExpression(tokens, 0);
    }

    /**
     * Parses the expression recursively and updates the tokens list by removing
     * consumed tokens.
     *
     * @param tokens        the list of tokens
     * @param minPrecedence the minimum precedence for the current parsing context
     * @return the parsed value (not used in validation)
     */
    private void parseExpression(List<String> tokens, int minPrecedence) {
        // Parse the left-hand side (lhs)
        parsePrimary(tokens);

        while (!tokens.isEmpty() && isOperator(tokens.get(0)) && OP_PRECEDENCE.get(tokens.get(0)) >= minPrecedence) {
            String operator = tokens.remove(0);
            int precedence = OP_PRECEDENCE.get(operator);
            // Parse the right-hand side (rhs) with higher precedence
            parseExpression(tokens, precedence + 1);
        }
    }

    /**
     * Parses a primary expression (number, cell reference, or parenthesized
     * expression).
     *
     * @param tokens the list of tokens
     */
    private void parsePrimary(List<String> tokens) {
        if (tokens.isEmpty()) {
            throw new IllegalArgumentException("Unexpected end of expression");
        }

        String token = tokens.remove(0);
        if (token.equals("(")) {
            parseExpression(tokens, 0);
            if (tokens.isEmpty() || !tokens.remove(0).equals(")")) {
                throw new IllegalArgumentException("Expected closing parenthesis");
            }
        } else if (isNumber(token) || isCellReference(token)) {
            // Valid primary expression
        } else {
            throw new IllegalArgumentException("Expected number, cell reference, or parenthesis");
        }
    }

    /**
     * Checks if the token is an operator.
     *
     * @param token the token to check
     * @return true if it's an operator, false otherwise
     */
    private boolean isOperator(String token) {
        return OP_PRECEDENCE.containsKey(token);
    }

    /**
     * Checks if the token is a cell reference.
     *
     * @param token the token to check
     * @return true if it's a cell reference, false otherwise
     */
    private boolean isCellReference(String token) {
        return token.matches("[A-Za-z]+\\d+");
    }

}
