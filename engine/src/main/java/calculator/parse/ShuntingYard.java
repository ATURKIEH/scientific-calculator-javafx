package calculator.parse;

import java.util.ArrayList;
import java.util.Stack;

import calculator.token.Token;
import calculator.token.TokenType;
import calculator.util.Operators;

public class ShuntingYard {

    public static ArrayList<Token> toPostfix(ArrayList<Token> infix) {
        ArrayList<Token> output = new ArrayList<>();
        Stack<Token> opStack = new Stack<>();

        for (Token token : infix) {
            switch (token.getType()) {

                case NUMBER:
                    output.add(token);
                    break;

                case FUNCTION:
                    opStack.push(token);
                    break;

                case OPERATOR: {
                    String curOp = token.getText();

                    while (!opStack.isEmpty()) {
                        Token top = opStack.peek();

                        // Functions always pop before operators
                        if (top.getType() == TokenType.FUNCTION) {
                            output.add(opStack.pop());
                            continue;
                        }

                        // Only compare precedence/associativity against operators
                        if (top.getType() == TokenType.OPERATOR) {
                            String topOp = top.getText();

                            boolean higherPrec =
                                    Operators.precedence(topOp) > Operators.precedence(curOp);

                            boolean samePrecAndLeftAssoc =
                                    Operators.precedence(topOp) == Operators.precedence(curOp) &&
                                    Operators.associativity(curOp) == Operators.Associativity.LEFT;

                            if (higherPrec || samePrecAndLeftAssoc) {
                                output.add(opStack.pop());
                                continue;
                            }
                        }

                        // If top is not FUNCTION and not an OPERATOR that should pop, stop.
                        break;
                    }

                    // Push current operator once after popping
                    opStack.push(token);
                    break;
                }

                case LPAREN:
                    opStack.push(token);
                    break;

                case RPAREN:
                    // Pop until left paren
                    while (!opStack.isEmpty() && opStack.peek().getType() != TokenType.LPAREN) {
                        output.add(opStack.pop());
                    }

                    if (opStack.isEmpty()) {
                        throw new IllegalArgumentException("Mismatched parentheses");
                    }

                    // Pop/discard the LPAREN
                    opStack.pop();

                    // If a function is on top, pop it too
                    if (!opStack.isEmpty() && opStack.peek().getType() == TokenType.FUNCTION) {
                        output.add(opStack.pop());
                    }
                    break;
            }
        }

        // Drain remaining operators
        while (!opStack.isEmpty()) {
            Token top = opStack.pop();
            if (top.getType() == TokenType.LPAREN || top.getType() == TokenType.RPAREN) {
                throw new IllegalArgumentException("Mismatched parentheses");
            }
            output.add(top);
        }

        return output;
    }
}
