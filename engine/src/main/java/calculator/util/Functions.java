package calculator.util;

public class Functions {
    public static boolean isFunction(String func){
        return func.equals("sin") || func.equals("cos") || func.equals("tan") ||
               func.equals("log") || func.equals("ln") || func.equals("sqrt");
    }
}
