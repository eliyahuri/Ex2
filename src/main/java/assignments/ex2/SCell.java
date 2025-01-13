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
            Parser parser = new Parser(tokens);
            parser.parseExpression();
            // After parsing, there should be no remaining tokens
            return parser.isAtEnd();
        } catch (ParseException e) {
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
     * @throws ParseException if an invalid token is encountered
     */
    private List<String> tokenize(String expr) throws ParseException {
        List<String> tokens = new ArrayList<>();
        int i = 0;
        while (i < expr.length()) {
            char c = expr.charAt(i);
            if (Character.isWhitespace(c)) {
                i++;
                continue;
            }

            if (c == '(' || c == ')' || OP_PRECEDENCE.containsKey(String.valueOf(c))) {
                tokens.add(String.valueOf(c));
                i++;
            } else if (Character.isDigit(c) || c == '.') {
                StringBuilder sb = new StringBuilder();
                boolean hasDot = false;
                while (i < expr.length() && (Character.isDigit(expr.charAt(i)) || expr.charAt(i) == '.')) {
                    if (expr.charAt(i) == '.') {
                        if (hasDot) {
                            throw new ParseException("Multiple decimal points in number");
                        }
                        hasDot = true;
                    }
                    sb.append(expr.charAt(i));
                    i++;
                }
                tokens.add(sb.toString());
            } else if (Character.isLetter(c)) {
                StringBuilder sb = new StringBuilder();
                sb.append(c);
                i++;
                while (i < expr.length() && Character.isLetterOrDigit(expr.charAt(i))) {
                    sb.append(expr.charAt(i));
                    i++;
                }
                tokens.add(sb.toString());
            } else {
                throw new ParseException("Invalid character encountered: " + c);
            }
        }
        return tokens;
    }

    /**
     * Custom exception for parsing errors.
     */
    private static class ParseException extends Exception {
        public ParseException(String message) {
            super(message);
        }
    }

    /**
     * Recursive descent parser for validating formulas.
     */
    private class Parser {
        private final List<String> tokens;
        private int position;

        public Parser(List<String> tokens) {
            this.tokens = tokens;
            this.position = 0;
        }

        public void parseExpression() throws ParseException {
            parseTerm();
            while (match("+", "-")) {
                parseTerm();
            }
        }

        private void parseTerm() throws ParseException {
            parseFactor();
            while (match("*", "/")) {
                parseFactor();
            }
        }

        private void parseFactor() throws ParseException {
            if (match("(")) {
                parseExpression();
                if (!match(")")) {
                    throw new ParseException("Expected closing parenthesis");
                }
            } else if (matchNumber() || matchCellReference()) {
                // Operand parsed successfully
            } else {
                throw new ParseException("Expected number, cell reference, or parenthesis");
            }
        }

        private boolean match(String... expected) {
            if (isAtEnd()) {
                return false;
            }
            String current = peek();
            for (String exp : expected) {
                if (current.equals(exp)) {
                    advance();
                    return true;
                }
            }
            return false;
        }

        private boolean matchNumber() {
            if (isAtEnd()) {
                return false;
            }
            String current = peek();
            try {
                Double.parseDouble(current);
                advance();
                return true;
            } catch (NumberFormatException e) {
                return false;
            }
        }

        private boolean matchCellReference() {
            if (isAtEnd()) {
                return false;
            }
            String current = peek();
            // Cell reference pattern: [A-Z]+[0-9]+
            if (current.matches("[A-Za-z]+\\d+")) {
                advance();
                return true;
            }
            return false;
        }

        private String peek() {
            return tokens.get(position);
        }

        private String advance() {
            return tokens.get(position++);
        }

        public boolean isAtEnd() {
            return position >= tokens.size();
        }
    }
}
