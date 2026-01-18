package calculator.eval;
import java.util.ArrayList;
import java.util.Stack;

import calculator.token.Token;
import calculator.token.TokenType;
import calculator.util.Operators;


public class PostfixEvaluator {
    public static double eval(ArrayList<Token> postfix){
        Stack<Double> stack = new Stack<>();
        for (Token t : postfix){
            switch(t.getType()){
                case NUMBER:
                    stack.push(t.getValue());
                    break;
                case OPERATOR:
                    String op = t.getText();
                    int n = Operators.arity(op);
                    if (stack.size() < n){
                        throw new IllegalArgumentException("Insufficient values in expression for operator: " + op);
                    }
                    else if( n == 1){
                        double a = stack.pop();
                        double result = -a;
                        stack.push(result);
                    }
                    else if( n == 2){
                        double b = stack.pop();
                        double a = stack.pop();
                        double result;
                        switch(op){
                            case "+":
                                result = a + b;
                                break;
                            case "-":
                                result = a - b;
                                break;
                            case "*":
                                result = a * b;
                                break;
                            case "/":
                                if ( b == 0 ) {
                                    throw new ArithmeticException("Division by zero");
                                }
                                result = a / b;
                                break;
                            case "^":
                                result = Math.pow(a, b);
                                break;
                            default:
                                throw new IllegalArgumentException("Unknown operator: " + op);
                        }
                        stack.push(result);
                    }
                    break;
                case FUNCTION:
                    String fn  = t.getText();
                    if (stack.isEmpty()){
                        throw new IllegalArgumentException("Missing argument for function: " + fn);
                    }
                    double x = stack.pop();
                    switch (fn){
                        case "sin":
                            stack.push(Math.sin(x));
                            break;
                        case "cos":
                            stack.push(Math.cos(x));
                            break;
                        case "tan":
                            stack.push(Math.tan(x));
                            break;
                        case "log":
                            if ( x <= 0 ) {
                                throw new ArithmeticException("Logarithm of non-positive number");
                            }
                            stack.push(Math.log10(x));
                            break;
                        case "ln":
                            if ( x <= 0 ) {
                                throw new ArithmeticException("Natural logarithm of non-positive number");
                            }
                            stack.push(Math.log(x));
                            break;
                        case "sqrt":
                            if ( x < 0 ) {
                                throw new ArithmeticException("Square root of negative number");
                            }
                            stack.push(Math.sqrt(x));
                            break;
                        default:
                            throw new IllegalArgumentException("Unknown function: " + fn);
                    }
                    break;
                default:
                    throw new IllegalArgumentException("Unexpected token type: " + t.getType());
                    
            }
        
        }
        if (stack.size() != 1) {
            throw new IllegalArgumentException("The user input has too many values.");
        }
        return stack.pop();
    }
}
