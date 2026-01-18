package calculator.token;
import java.util.ArrayList;

import calculator.error.CalcException;
import calculator.util.Functions;

public class Tokenizer {

    public ArrayList<Token> tokenize(String expr){
        ArrayList<Token> tokens = new ArrayList<>();

        for (int i = 0 ; i < expr.length(); i++) {
            char currentChar = expr.charAt(i);

            if (Character.isWhitespace(currentChar)) {
                continue;
            }

            // -------------------------
            // NUMBER (with optional scientific notation)
            // -------------------------
            else if (Character.isDigit(currentChar) || currentChar == '.') {
                int j = i;

                // digits + dot
                while (j < expr.length() &&
                        (Character.isDigit(expr.charAt(j)) || expr.charAt(j) == '.')) {
                    j++;
                }

                // scientific notation: e/E [+/-] digits
                if (j < expr.length() && (expr.charAt(j) == 'e' || expr.charAt(j) == 'E')) {
                    int k = j + 1;

                    if (k < expr.length() && (expr.charAt(k) == '+' || expr.charAt(k) == '-')) {
                        k++;
                    }

                    int startDigits = k;
                    while (k < expr.length() && Character.isDigit(expr.charAt(k))) {
                        k++;
                    }

                    // only accept exponent if it has at least 1 digit
                    if (k > startDigits) {
                        j = k;
                    }
                }

                String numberText = expr.substring(i, j);
                double value;
                try {
                    value = Double.parseDouble(numberText);
                } catch (NumberFormatException ex) {
                    throw new CalcException("Invalid number: " + numberText, ex);
                }

                Token numberToken = Token.number(value);
                maybeInsertImplicitMul(tokens, numberToken);
                tokens.add(numberToken);

                i = j - 1;
            }

            // -------------------------
            // IDENTIFIER (constants / functions)
            // -------------------------
            else if (Character.isLetter(currentChar)) {
                int j = i;
                while (j < expr.length() && Character.isLetter(expr.charAt(j))) {
                    j++;
                }

                String name = expr.substring(i, j).toLowerCase();

                // ✅ CONSTANTS
                if (name.equals("pi")) {
                    Token t = Token.number(Math.PI);
                    maybeInsertImplicitMul(tokens, t);
                    tokens.add(t);
                }
                else if (name.equals("e")) {
                    Token t = Token.number(Math.E);
                    maybeInsertImplicitMul(tokens, t);
                    tokens.add(t);
                }
                // ✅ FUNCTIONS
                else if (Functions.isFunction(name)) {
                    Token t = Token.function(name);
                    maybeInsertImplicitMul(tokens, t);
                    tokens.add(t);
                }
                else {
                    throw new CalcException("Unknown identifier: " + name);
                }

                i = j - 1;
            }

            // -------------------------
            // PARENS
            // -------------------------
            else if (currentChar == '(') {
                Token t = Token.lparen();
                maybeInsertImplicitMul(tokens, t);
                tokens.add(t);
            }

            else if (currentChar == ')') {
                tokens.add(Token.rparen());
            }

            // -------------------------
            // OPERATORS
            // -------------------------
            else {
                if (currentChar == '+' || currentChar == '-' ||
                    currentChar == '*' || currentChar == '/' ||
                    currentChar == '^') {

                    if (currentChar == '-' && isUnaryMinus(tokens)) {
                        tokens.add(Token.operator("NEG"));
                    } else {
                        tokens.add(Token.operator(Character.toString(currentChar)));
                    }
                }
                else {
                    throw new CalcException("Unknown character: " + currentChar);
                }
            }
        }

        return tokens;
    }

    private boolean isUnaryMinus(ArrayList<Token> tokens) {
        if (tokens.isEmpty()) return true;
        Token prev = tokens.get(tokens.size() - 1);
        return prev.getType() == TokenType.OPERATOR || prev.getType() == TokenType.LPAREN;
    }

    private void maybeInsertImplicitMul(ArrayList<Token> tokens, Token nextToken) {
        if (tokens.isEmpty()) return;

        Token prev = tokens.get(tokens.size() - 1);

        boolean prevCanEnd = (prev.getType() == TokenType.NUMBER || prev.getType() == TokenType.RPAREN);
        boolean nextCanStart = (nextToken.getType() == TokenType.NUMBER ||
                                nextToken.getType() == TokenType.FUNCTION ||
                                nextToken.getType() == TokenType.LPAREN);

        if (prevCanEnd && nextCanStart) {
            tokens.add(Token.operator("*"));
        }
    }
}
