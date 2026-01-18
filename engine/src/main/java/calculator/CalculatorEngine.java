package calculator;

import java.util.ArrayList;

import calculator.eval.PostfixEvaluator;
import calculator.parse.ShuntingYard;
import calculator.token.Token;
import calculator.token.Tokenizer;

public class CalculatorEngine {
    private final Tokenizer tokenizer;

    public CalculatorEngine() {
        this.tokenizer = new Tokenizer();
    }
    public double evaluate(String expr){
        if (expr == null){
            throw new IllegalArgumentException("Expression cannot be null");
        }
        else if (expr.trim().isEmpty()){
            throw new IllegalArgumentException("Expression cannot be empty");
        }
        else{
            ArrayList<Token> tokens = tokenizer.tokenize(expr);
            ArrayList<Token> postfix = ShuntingYard.toPostfix(tokens);
            double result = PostfixEvaluator.eval(postfix);
            if (result == -0.0){
                result = 0.0;
            }
            return result;
        }
    }
    public ArrayList<Token> toPostfixTokens(String expr){
        ArrayList<Token> tokens = tokenizer.tokenize(expr);
        return ShuntingYard.toPostfix(tokens);
    }
}