import java.io.*;
import java.net.*;
import java.util.Scanner;
import org.json.JSONObject;

public class PhishingDetectorClient {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 8080;

    public static void main(String[] args) {
        try (
                Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
                PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                Scanner scanner = new Scanner(System.in)
        ) {
            System.out.println("🔍 Enter a URL to check, or type:");
            System.out.println("    • 'history' to view checked URLs");
            System.out.println("    • 'clear' to clear the history");
            System.out.println("    • 'exit' to quit the program");

            while (true) {
                System.out.print("🌐 URL: ");
                String input = scanner.nextLine().trim();

                if (input.equalsIgnoreCase("exit")) {
                    System.out.println("🔴 Exiting...");
                    break;
                }

                if (input.isEmpty()) {
                    System.out.println("⚠ Please enter a valid URL.");
                    continue;
                }

                writer.println(input);

                StringBuilder responseBuilder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    responseBuilder.append(line);
                    if (!reader.ready()) break;
                }

                try {
                    JSONObject response = new JSONObject(responseBuilder.toString());

                    if (input.equalsIgnoreCase("history")) {
                        System.out.println("📜 History:\n" + response.optString("history", "No history found."));
                    } else if (input.equalsIgnoreCase("clear")) {
                        System.out.println("🧹 " + response.optString("message", "History cleared."));
                    } else {
                        String status = response.optString("status", "UNKNOWN");
                        if ("PHISHING".equalsIgnoreCase(status)) {
                            System.out.println("⚠ ALERT: The URL is **PHISHING**!");
                        } else if ("SAFE".equalsIgnoreCase(status)) {
                            System.out.println("✅ SAFE: The URL is **SAFE**.");
                        } else {
                            System.out.println("❓ Could not determine status.");
                        }
                    }
                } catch (Exception e) {
                    System.out.println("❌ Invalid response from server.");
                }
            }
        } catch (IOException e) {
            System.out.println("❌ Error connecting to the server: " + e.getMessage());
        }
    }
}
