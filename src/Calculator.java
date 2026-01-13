import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class Calculator extends JFrame implements ActionListener {
    private final JTextField display;
    private final JButton[] buttons;
    private final String[] buttonLabels = {
            "7", "8", "9", "/", "√",
            "4", "5", "6", "*", "x²",
            "1", "2", "3", "-", "sin",
            "0", ".", "=", "+", "cos",
            "C", "Del", "tan", "log", "ln"
    };
    private String input = "";
    private double num1, num2;
    private char operator;

    public Calculator() {
        setTitle("Scientific Calculator");
        setSize(400, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        display = new JTextField();
        display.setFont(new Font("Arial", Font.BOLD, 20));
        display.setHorizontalAlignment(JTextField.RIGHT);
        display.setEditable(false);
        add(display, BorderLayout.NORTH);

        JPanel panel = new JPanel(new GridLayout(5, 5, 5, 5));
        buttons = new JButton[buttonLabels.length];

        for (int i = 0; i < buttonLabels.length; i++) {
            buttons[i] = new JButton(buttonLabels[i]);
            buttons[i].setFont(new Font("Arial", Font.BOLD, 18));
            buttons[i].addActionListener(this);
            panel.add(buttons[i]);
        }
        add(panel, BorderLayout.CENTER);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();

        if (command.matches("[0-9]")) { // Number buttons
            input += command;
            display.setText(input);
        } else if (command.equals(".")) { // Decimal point
            if (!input.contains(".")) {
                input += ".";
                display.setText(input);
            }
        } else if (command.matches("[+\\-*/]")) { // Basic operations
            num1 = Double.parseDouble(input);
            operator = command.charAt(0);
            input = "";
        } else if (command.equals("=")) { // Equals button
            num2 = Double.parseDouble(input);
            double result = calculate(num1, num2, operator);
            display.setText(formatResult(result));
            input = String.valueOf(result);
        } else if (command.equals("C")) { // Clear
            input = "";
            display.setText("");
        } else if (command.equals("Del")) { // Delete last character
            if (!input.isEmpty()) {
                input = input.substring(0, input.length() - 1);
                display.setText(input);
            }
        } else if (command.equals("√")) { // Square root
            double result = Math.sqrt(Double.parseDouble(input));
            display.setText(formatResult(result));
            input = String.valueOf(result);
        } else if (command.equals("x²")) { // Square
            double result = Math.pow(Double.parseDouble(input), 2);
            display.setText(formatResult(result));
            input = String.valueOf(result);
        } else if (command.equals("sin")) { // Sine
            double result = Math.sin(Math.toRadians(Double.parseDouble(input)));
            display.setText(formatResult(result));
            input = String.valueOf(result);
        } else if (command.equals("cos")) { // Cosine
            double result = Math.cos(Math.toRadians(Double.parseDouble(input)));
            display.setText(formatResult(result));
            input = String.valueOf(result);
        } else if (command.equals("tan")) { // Tangent
            double result = Math.tan(Math.toRadians(Double.parseDouble(input)));
            display.setText(formatResult(result));
            input = String.valueOf(result);
        } else if (command.equals("log")) { // Logarithm (base 10)
            double result = Math.log10(Double.parseDouble(input));
            display.setText(formatResult(result));
            input = String.valueOf(result);
        } else if (command.equals("ln")) { // Natural logarithm
            double result = Math.log(Double.parseDouble(input));
            display.setText(formatResult(result));
            input = String.valueOf(result);
        }
    }

    private double calculate(double a, double b, char op) {
        return switch (op) {
            case '+' -> a + b;
            case '-' -> a - b;
            case '*' -> a * b;
            case '/' -> (b != 0) ? a / b : 0; // Prevent division by zero
            default -> 0;
        };
    }

    private String formatResult(double result) {
        BigDecimal bd = new BigDecimal(result).setScale(10, RoundingMode.HALF_UP);
        return bd.stripTrailingZeros().toPlainString();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Calculator calculator = new Calculator();
            calculator.setVisible(true);
        });
    }
}
