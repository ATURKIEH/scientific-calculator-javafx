package calculator.token;

public class Token {

    private final TokenType type;
    private final String text;
    private final Double value;

    private Token(TokenType type, String text, Double value) {
        this.type = type;
        this.text = text;
        this.value = value;
    }

    public static Token number(double value) {
        return new Token(TokenType.NUMBER, Double.toString(value), value);
    }

    public static Token operator(String op) {
        return new Token(TokenType.OPERATOR, op, null);
    }

    public static Token function(String name) {
        return new Token(TokenType.FUNCTION, name, null);
    }

    public static Token lparen() {
        return new Token(TokenType.LPAREN, "(", null);
    }

    public static Token rparen() {
        return new Token(TokenType.RPAREN, ")", null);
    }

    public TokenType getType() {
        return type;
    }

    public String getText() {
        return text;
    }

    public Double getValue() {
        return value;
    }

    @Override
    public String toString() {
        if (type == TokenType.NUMBER) {
            return "NUMBER(" + value + ")";
        }
        return type + "(" + text + ")";
    }
}
