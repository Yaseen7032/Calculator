import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

public final class CalculatorApp extends JFrame {
    private static final Color CANVAS = new Color(242, 241, 235);
    private static final Color DISPLAY = new Color(31, 43, 38);
    private static final Color INK = new Color(35, 42, 38);
    private static final Color KEY = new Color(255, 255, 251);
    private static final Color UTILITY = new Color(220, 226, 218);
    private static final Color OPERATOR = new Color(43, 112, 93);
    private static final Color EQUALS = new Color(218, 111, 77);

    private final StringBuilder expression = new StringBuilder();
    private final JTextField expressionField = new JTextField();
    private final JLabel resultLabel = new JLabel("0", SwingConstants.RIGHT);

    private CalculatorApp() {
        super("Everyday Calculator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(340, 500));
        setSize(380, 560);
        setLocationRelativeTo(null);
        getContentPane().setBackground(CANVAS);
        setLayout(new BorderLayout(0, 18));

        add(createDisplay(), BorderLayout.NORTH);
        add(createKeypad(), BorderLayout.CENTER);
        installKeyboardActions();
    }

    private JPanel createDisplay() {
        JPanel display = new JPanel(new BorderLayout(8, 12));
        display.setBackground(DISPLAY);
        display.setBorder(BorderFactory.createEmptyBorder(22, 22, 18, 22));

        JLabel title = new JLabel("CALCULATOR");
        title.setForeground(new Color(174, 193, 180));
        title.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 11));

        expressionField.setEditable(false);
        expressionField.setFocusable(false);
        expressionField.setHorizontalAlignment(SwingConstants.RIGHT);
        expressionField.setBorder(BorderFactory.createEmptyBorder());
        expressionField.setOpaque(false);
        expressionField.setForeground(new Color(211, 221, 213));
        expressionField.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 20));

        resultLabel.setForeground(Color.WHITE);
        resultLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 38));

        JPanel readout = new JPanel(new BorderLayout(4, 4));
        readout.setOpaque(false);
        readout.add(expressionField, BorderLayout.NORTH);
        readout.add(resultLabel, BorderLayout.CENTER);

        display.add(title, BorderLayout.NORTH);
        display.add(readout, BorderLayout.CENTER);
        return display;
    }

    private JPanel createKeypad() {
        JPanel keypad = new JPanel(new GridLayout(5, 4, 10, 10));
        keypad.setBackground(CANVAS);
        keypad.setBorder(BorderFactory.createEmptyBorder(0, 18, 18, 18));

        String[][] keys = {
            {"AC", "DEL", "(", ")"},
            {"7", "8", "9", "÷"},
            {"4", "5", "6", "×"},
            {"1", "2", "3", "−"},
            {"0", ".", "=", "+"}
        };

        for (String[] row : keys) {
            for (String key : row) {
                JButton button = createButton(key);
                button.addActionListener(event -> handleKey(key));
                keypad.add(button);
            }
        }
        return keypad;
    }

    private JButton createButton(String text) {
        if (text == null) {
            throw new IllegalArgumentException("Button label must not be null");
        }
        JButton button = new JButton(text);
        Color background = switch (text) {
            case "AC", "DEL", "(", ")" -> UTILITY;
            case "÷", "×", "−", "+" -> OPERATOR;
            case "=" -> EQUALS;
            default -> KEY;
        };
        button.setBackground(background);
        button.setForeground((background == OPERATOR || background == EQUALS) ? Color.WHITE : INK);
        button.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setMargin(new Insets(8, 8, 8, 8));
        button.setToolTipText(switch (text) {
            case "AC" -> "Clear all";
            case "DEL" -> "Delete last character";
            case "=" -> "Evaluate expression";
            default -> null;
        });
        return button;
    }

    private void handleKey(String key) {
        switch (key) {
            case "AC" -> clear();
            case "DEL" -> deleteLast();
            case "=" -> evaluate();
            default -> append(switch (key) {
                case "÷" -> "/";
                case "×" -> "*";
                case "−" -> "-";
                default -> key;
            });
        }
    }

    private void append(String value) {
        expression.append(value);
        refreshDisplay();
    }

    private void clear() {
        expression.setLength(0);
        resultLabel.setText("0");
        expressionField.setText("");
    }

    private void deleteLast() {
        if (!expression.isEmpty()) {
            expression.deleteCharAt(expression.length() - 1);
        }
        refreshDisplay();
    }

    private void evaluate() {
        if (expression.isEmpty()) {
            return;
        }
        try {
            String result = formatResult(evaluateExpression(expression.toString()));
            expression.setLength(0);
            expression.append(result);
            refreshDisplay();
        } catch (ArithmeticException exception) {
            resultLabel.setText("Cannot divide by zero");
        } catch (IllegalArgumentException exception) {
            resultLabel.setText("Invalid expression");
        }
    }

    private void refreshDisplay() {
        expressionField.setText(expression.toString()
                .replace("*", " × ").replace("/", " ÷ ").replace("-", " − ").replace("+", " + "));
        if (expression.isEmpty()) {
            resultLabel.setText("0");
            return;
        }
        try {
            resultLabel.setText(formatResult(evaluateExpression(expression.toString())));
        } catch (IllegalArgumentException | ArithmeticException exception) {
            resultLabel.setText(" ");
        }
    }

    private void installKeyboardActions() {
        Map<String, Runnable> actions = new LinkedHashMap<>();
        for (char digit = '0'; digit <= '9'; digit++) {
            String value = Character.toString(digit);
            actions.put(value, () -> append(value));
        }
        actions.put(".", () -> append("."));
        actions.put("+", () -> append("+"));
        actions.put("-", () -> append("-"));
        actions.put("*", () -> append("*"));
        actions.put("/", () -> append("/"));
        actions.put("(", () -> append("("));
        actions.put(")", () -> append(")"));
        actions.put("BACK_SPACE", this::deleteLast);
        actions.put("ESCAPE", this::clear);
        actions.put("ENTER", this::evaluate);

        actions.forEach((key, action) -> {
            KeyStroke stroke = key.length() == 1
                    ? KeyStroke.getKeyStroke(key.charAt(0))
                    : KeyStroke.getKeyStroke("pressed " + key);
            String actionName = "calculator-" + key;
            getRootPane().getInputMap(JPanel.WHEN_IN_FOCUSED_WINDOW).put(stroke, actionName);
            getRootPane().getActionMap().put(actionName, new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent event) {
                    action.run();
                }
            });
        });
    }

    static double evaluateExpression(String source) {
        return new ExpressionParser(source).parse();
    }

    private static String formatResult(double value) {
        if (!Double.isFinite(value)) {
            throw new IllegalArgumentException("Result is outside the supported range");
        }
        if (value == 0) {
            return "0";
        }
        return BigDecimal.valueOf(value).stripTrailingZeros().toPlainString();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CalculatorApp().setVisible(true));
    }

    private static final class ExpressionParser {
        private final String source;
        private int position;

        private ExpressionParser(String source) {
            this.source = source;
        }

        private double parse() {
            double value = parseExpression();
            skipWhitespace();
            if (position != source.length()) {
                throw invalidExpression();
            }
            return value;
        }

        private double parseExpression() {
            double value = parseTerm();
            while (true) {
                skipWhitespace();
                if (consume('+')) {
                    value += parseTerm();
                } else if (consume('-')) {
                    value -= parseTerm();
                } else {
                    return value;
                }
            }
        }

        private double parseTerm() {
            double value = parseUnary();
            while (true) {
                skipWhitespace();
                if (consume('*')) {
                    value *= parseUnary();
                } else if (consume('/')) {
                    double divisor = parseUnary();
                    if (divisor == 0) {
                        throw new ArithmeticException("Division by zero");
                    }
                    value /= divisor;
                } else {
                    return value;
                }
            }
        }

        private double parseUnary() {
            skipWhitespace();
            if (consume('+')) {
                return parseUnary();
            }
            if (consume('-')) {
                return -parseUnary();
            }
            return parsePrimary();
        }

        private double parsePrimary() {
            skipWhitespace();
            if (consume('(')) {
                double value = parseExpression();
                skipWhitespace();
                if (!consume(')')) {
                    throw invalidExpression();
                }
                return value;
            }

            int start = position;
            boolean hasDigit = false;
            while (position < source.length() && Character.isDigit(source.charAt(position))) {
                position++;
                hasDigit = true;
            }
            if (consume('.')) {
                while (position < source.length() && Character.isDigit(source.charAt(position))) {
                    position++;
                    hasDigit = true;
                }
            }
            if (!hasDigit) {
                throw invalidExpression();
            }
            return Double.parseDouble(source.substring(start, position));
        }

        private boolean consume(char expected) {
            if (position < source.length() && source.charAt(position) == expected) {
                position++;
                return true;
            }
            return false;
        }

        private void skipWhitespace() {
            while (position < source.length() && Character.isWhitespace(source.charAt(position))) {
                position++;
            }
        }

        private IllegalArgumentException invalidExpression() {
            return new IllegalArgumentException("Invalid expression at position " + position);
        }
    }
}