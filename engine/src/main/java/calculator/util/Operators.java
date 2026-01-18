package calculator.util;



public class Operators {
    public enum Associativity {
    LEFT,
    RIGHT
    }
    public static boolean isOperator(String op){
        return op.equals("NEG") || op.equals("+") || op.equals("-") || op.equals("*") || op.equals("/") || op.equals("^");
    }
    public static int precedence(String op){
        switch(op){
            case "NEG":
                return 5;
            case "^":
                return 4;
            case "*":
            case "/":
                return 3;
            case "+":
            case "-":
                return 2;
            default:
                throw new IllegalArgumentException("Unknown operator: " + op);
        }
    }
    public static Associativity associativity(String op){
        switch(op){
            case "NEG":
                return Associativity.RIGHT;
            case "^":
                return Associativity.RIGHT;
            case "*":
            case "/":
            case "+":
            case "-":
                return Associativity.LEFT;
            default:
                throw new IllegalArgumentException("Unknown operator: " + op);
        }
    }
    public static int arity(String op){
        switch(op){
            case "NEG":
                return 1;
            case "^":
            case "*":
            case "/":
            case "+":
            case "-":
                return 2;
            default:
                throw new IllegalArgumentException("Unknown operator: " + op);
        }
    }
}


