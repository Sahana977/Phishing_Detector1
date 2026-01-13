import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;
import org.json.*;

public class PhishingDetectorGUI extends JFrame {
    private JTextField urlField;
    private JTextArea historyArea;
    private JLabel resultLabel;
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 8080;

    public PhishingDetectorGUI() {
        setTitle("🔍 Phishing Detector");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Input panel
        JPanel inputPanel = new JPanel(new FlowLayout());
        urlField = new JTextField(25);
        JButton checkButton = new JButton("Check URL");
        JButton historyButton = new JButton("View History");
        JButton clearHistoryButton = new JButton("Clear History");

        inputPanel.add(new JLabel("🌐 URL: "));
        inputPanel.add(urlField);
        inputPanel.add(checkButton);
        inputPanel.add(historyButton);
        inputPanel.add(clearHistoryButton);

        // Result label
        resultLabel = new JLabel("Enter a URL and click 'Check'");
        resultLabel.setHorizontalAlignment(JLabel.CENTER);

        // History area
        historyArea = new JTextArea(10, 50);
        historyArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(historyArea);

        // Add components
        add(inputPanel, BorderLayout.NORTH);
        add(resultLabel, BorderLayout.CENTER);
        add(scrollPane, BorderLayout.SOUTH);

        // Button actions
        checkButton.addActionListener(e -> checkURL());
        historyButton.addActionListener(e -> fetchHistory());
        clearHistoryButton.addActionListener(e -> clearHistory());

        setVisible(true);
    }

    private void checkURL() {
        String url = urlField.getText().trim();
        if (url.isEmpty()) {
            resultLabel.setText("⚠ Please enter a URL!");
            resultLabel.setForeground(Color.BLACK);
            return;
        }

        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
             PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            writer.println(url);
            String response = reader.readLine();
            JSONObject jsonResponse = new JSONObject(response);
            String status = jsonResponse.getString("status");

            resultLabel.setText(status.equals("PHISHING") ? "⚠ ALERT: PHISHING SITE!" : "✅ SAFE: This URL is safe.");
            resultLabel.setForeground(status.equals("PHISHING") ? Color.RED : Color.GREEN);
        } catch (Exception e) {
            resultLabel.setText("❌ Error connecting to server!");
            resultLabel.setForeground(Color.RED);
        }
    }

    private void fetchHistory() {
        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
             PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            writer.println("GET_HISTORY");
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            JSONObject jsonResponse = new JSONObject(response.toString());
            historyArea.setText
        (jsonResponse.optString("history", "📁 No history found."));
        } catch (Exception e) {
            historyArea.setText("❌ Error fetching history: " + e.getMessage());
        }
    }

    private void clearHistory() {
        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
             PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            writer.println("CLEAR_HISTORY");
            String response = reader.readLine();
            JSONObject jsonResponse = new JSONObject(response);
            historyArea.setText("✅ " + jsonResponse.optString("message", "History cleared."));
        } catch (Exception e) {
            historyArea.setText("❌ Error clearing history: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(PhishingDetectorGUI::new);
    }
}
