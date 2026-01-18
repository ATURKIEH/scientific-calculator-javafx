package ui;

import calculator.CalculatorEngine;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class App extends Application {

    private final CalculatorEngine engine = new CalculatorEngine();
    private TextField display;

    @Override
    public void start(Stage stage) {

        // ---------- ROOT ----------
        VBox root = new VBox();
        root.setPadding(new Insets(15));
        root.setSpacing(12);

        // ---------- DISPLAY ----------
        display = new TextField("0");
        display.getStyleClass().add("display");
        display.setEditable(false);
        display.setPrefHeight(60);
        display.setAlignment(Pos.CENTER_RIGHT);

        // ---------- GRID ----------
        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(12);
        grid.setPadding(new Insets(6));

        grid.setAlignment(Pos.CENTER);

        // Row 0 (parens + backspace + clear)
        addBtn(grid, "(", 0, 0, () -> appendParen("("));
        addBtn(grid, ")", 1, 0, () -> appendParen(")"));
        addBtn(grid, "⌫", 2, 0, this::backspace);
        addBtn(grid, "C", 3, 0, this::clearDisplay);

        // Row 1
        addBtn(grid, "7", 0, 1, () -> appendDigit(7));
        addBtn(grid, "8", 1, 1, () -> appendDigit(8));
        addBtn(grid, "9", 2, 1, () -> appendDigit(9));
        addBtn(grid, "+", 3, 1, () -> appendOperator("+"));

        // Row 2
        addBtn(grid, "4", 0, 2, () -> appendDigit(4));
        addBtn(grid, "5", 1, 2, () -> appendDigit(5));
        addBtn(grid, "6", 2, 2, () -> appendDigit(6));
        addBtn(grid, "-", 3, 2, () -> appendOperator("-"));

        // Row 3
        addBtn(grid, "1", 0, 3, () -> appendDigit(1));
        addBtn(grid, "2", 1, 3, () -> appendDigit(2));
        addBtn(grid, "3", 2, 3, () -> appendDigit(3));
        addBtn(grid, "*", 3, 3, () -> appendOperator("*"));

        // Row 4
        addBtn(grid, "0", 0, 4, () -> appendDigit(0));
        addBtn(grid, ".", 1, 4, this::appendDecimal);
        addBtn(grid, "=", 2, 4, this::evaluateAndShow);
        

        addBtn(grid, "/", 3, 4, () -> appendOperator("/"));

        // Row 5 (functions + power)
        addBtn(grid, "sin", 0, 5, () -> appendFunction("sin"));
        addBtn(grid, "cos", 1, 5, () -> appendFunction("cos"));
        addBtn(grid, "tan", 2, 5, () -> appendFunction("tan"));
        addBtn(grid, "^",   3, 5, () -> appendOperator("^"));

        // Row 6 (more functions)
        addBtn(grid, "sqrt", 0, 6, () -> appendFunction("sqrt"));
        addBtn(grid, "log",  1, 6, () -> appendFunction("log"));
        addBtn(grid, "ln",   2, 6, () -> appendFunction("ln"));
        // col 3 intentionally empty

        root.getChildren().addAll(display, grid);

        Scene scene = new Scene(root, 420, 560);
        scene.getStylesheets().add(getClass().getResource("/ui/style.css").toExternalForm());


        // ---------- KEYBOARD INPUT ----------
        scene.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
            KeyCode code = e.getCode();

            // digits (top row)
            if (code.isDigitKey()) {
                String t = e.getText();
                if (t != null && t.length() == 1 && Character.isDigit(t.charAt(0))) {
                    appendDigit(t.charAt(0) - '0');
                    e.consume();
                }
                return;
            }

            // digits (numpad)
            if (code.isKeypadKey()) {
                switch (code) {
                    case NUMPAD0 -> appendDigit(0);
                    case NUMPAD1 -> appendDigit(1);
                    case NUMPAD2 -> appendDigit(2);
                    case NUMPAD3 -> appendDigit(3);
                    case NUMPAD4 -> appendDigit(4);
                    case NUMPAD5 -> appendDigit(5);
                    case NUMPAD6 -> appendDigit(6);
                    case NUMPAD7 -> appendDigit(7);
                    case NUMPAD8 -> appendDigit(8);
                    case NUMPAD9 -> appendDigit(9);
                    case ADD -> appendOperator("+");
                    case SUBTRACT -> appendOperator("-");
                    case MULTIPLY -> appendOperator("*");
                    case DIVIDE -> appendOperator("/");
                    default -> { return; }
                }
                e.consume();
                return;
            }

            switch (code) {
                case ENTER, EQUALS -> { evaluateAndShow(); e.consume(); }
                case BACK_SPACE -> { backspace(); e.consume(); }
                case ESCAPE -> { clearDisplay(); e.consume(); }
                case PERIOD, DECIMAL -> { appendDecimal(); e.consume(); }
                case OPEN_BRACKET, DIGIT9 -> { // (  on some layouts is Shift+9
                    if (e.isShiftDown() || code == KeyCode.OPEN_BRACKET) { appendParen("("); e.consume(); }
                }
                case CLOSE_BRACKET, DIGIT0 -> { // )  on some layouts is Shift+0
                    if (e.isShiftDown() || code == KeyCode.CLOSE_BRACKET) { appendParen(")"); e.consume(); }
                }
                case PLUS -> { appendOperator("+"); e.consume(); }
                case MINUS -> { appendOperator("-"); e.consume(); }
                case SLASH -> { appendOperator("/"); e.consume(); }
                case ASTERISK -> { appendOperator("*"); e.consume(); }
                default -> {
                    // also handle characters like '+' '-' '*' '/' '^' '(' ')'
                    String t = e.getText();
                    if (t != null && t.length() == 1) {
                        char c = t.charAt(0);
                        if (c == '+') { appendOperator("+"); e.consume(); }
                        else if (c == '-') { appendOperator("-"); e.consume(); }
                        else if (c == '*') { appendOperator("*"); e.consume(); }
                        else if (c == '/') { appendOperator("/"); e.consume(); }
                        else if (c == '^') { appendOperator("^"); e.consume(); }
                        else if (c == '(') { appendParen("("); e.consume(); }
                        else if (c == ')') { appendParen(")"); e.consume(); }
                        else if (c == '.') { appendDecimal(); e.consume(); }
                    }
                }
            }
        });

        stage.setTitle("Calculator");
        stage.setScene(scene);
        stage.show();
    }

    // ---------- UI HELPERS ----------
    private void addBtn(GridPane grid, String text, int col, int row, Runnable action) {
        Button b = new Button(text);
        b.setPrefSize(90, 62);
        b.setMinSize(90, 62);
        b.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        b.getStyleClass().add("btn");

        // categorize button styles
        if (text.equals("=")) b.getStyleClass().add("btn-eq");
        else if (text.equals("C")) b.getStyleClass().add("btn-danger");
        else if (text.equals("+") || text.equals("-") || text.equals("*") || text.equals("/") || text.equals("^"))
            b.getStyleClass().add("btn-op");
        else if (text.equals("sin") || text.equals("cos") || text.equals("tan") || text.equals("sqrt") || text.equals("log") || text.equals("ln"))
            b.getStyleClass().add("btn-fn");

        b.setOnAction(e -> action.run());
        grid.add(b, col, row);
    }


    // ---------- LOGIC ----------
    private void appendDigit(int digit) {
        String cur = display.getText();
        if (cur.equals("0")) display.setText(String.valueOf(digit));
        else display.setText(cur + digit);
    }

    private void appendDecimal() {
        String cur = display.getText();

        // find the current "number chunk" (since you add operators with spaces)
        int lastSpace = cur.lastIndexOf(' ');
        String chunk = (lastSpace == -1) ? cur : cur.substring(lastSpace + 1);

        // if chunk already has '.', do nothing
        if (chunk.contains(".")) return;

        // if last char is space (meaning you just typed an operator), start number with 0.
        if (!cur.isEmpty() && cur.charAt(cur.length() - 1) == ' ') {
            display.setText(cur + "0.");
            return;
        }

        // normal append
        display.setText(cur + ".");
    }

    private void clearDisplay() {
        display.setText("0");
    }

    private void backspace() {
        String cur = display.getText();
        if (cur.equals("0") || cur.isEmpty()) return;

        // If your display ends with " op " (3 chars), remove that whole operator chunk
        if (endsWithSpacedOperator(cur)) {
            cur = cur.substring(0, cur.length() - 3).trim();
            if (cur.isEmpty()) cur = "0";
            display.setText(cur);
            return;
        }

        // Otherwise remove one char
        cur = cur.substring(0, cur.length() - 1);

        // trim leftover spaces
        cur = cur.stripTrailing();
        if (cur.isEmpty()) cur = "0";

        display.setText(cur);
    }

    private void appendOperator(String op) {
        String cur = display.getText().trim();

        if (cur.equals("0")) {
            // allow: 0 + ...
            display.setText("0 " + op + " ");
            return;
        }

        if (endsWithSpacedOperator(cur + " ")) {
            // replace last operator
            String replaced = (cur + " ").substring(0, (cur + " ").length() - 3).trim();
            display.setText(replaced + " " + op + " ");
            return;
        }

        display.setText(cur + " " + op + " ");
    }

    private void appendParen(String p) {
        String cur = display.getText();

        if (cur.equals("0")) {
            display.setText(p);
            return;
        }

        // if last char is a digit or ')', and user types '(' -> insert implicit "* ("
        char last = cur.charAt(cur.length() - 1);
        if (p.equals("(") && (Character.isDigit(last) || last == ')')) {
            display.setText(cur + " * (");
            return;
        }

        // if last char is space, just append directly
        if (last == ' ') {
            display.setText(cur + p);
        } else {
            display.setText(cur + p);
        }
    }

    private void appendFunction(String fn) {
        // add like: sin(   cos(   sqrt(
        String cur = display.getText();

        if (cur.equals("0")) {
            display.setText(fn + "(");
            return;
        }

        char last = cur.charAt(cur.length() - 1);
        if (Character.isDigit(last) || last == ')') {
            // implicit multiply: 2sin( -> 2 * sin(
            display.setText(cur + " * " + fn + "(");
        } else if (last == ' ') {
            display.setText(cur + fn + "(");
        } else {
            display.setText(cur + fn + "(");
        }
    }

    private void evaluateAndShow() {
        try {
            String expr = display.getText().trim();

            // if expression ends with operator chunk, remove it safely
            if (endsWithSpacedOperator(expr + " ")) {
                expr = (expr + " ").substring(0, (expr + " ").length() - 3).trim();
            }

            if (expr.isEmpty()) {
                display.setText("0");
                return;
            }

            double result = engine.evaluate(expr);

            // pretty print ints
            if (result == (long) result) display.setText(String.valueOf((long) result));
            else display.setText(String.valueOf(result));

        } catch (Exception ex) {
            display.setText("Error");
            ex.printStackTrace();
        }
    }

    private boolean endsWithSpacedOperator(String s) {
        return s.endsWith(" + ") || s.endsWith(" - ") || s.endsWith(" * ") ||
               s.endsWith(" / ") || s.endsWith(" ^ ");
    }

    public static void main(String[] args) {
        launch(args);
    }
}
