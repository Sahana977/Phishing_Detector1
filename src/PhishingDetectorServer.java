import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;
import org.json.*;
import java.util.Base64;

public class PhishingDetectorServer {
    private static final int PORT = 8080;
    private static final String LOG_FILE = "phishing_logs.txt";
    private static final String VIRUSTOTAL_API_KEY = System.getenv("VIRUSTOTAL_API_KEY");
    private static final String VIRUSTOTAL_API_URL = "https://www.virustotal.com/api/v3/urls";

    private static final ExecutorService executor = Executors.newFixedThreadPool(10);

    public static void main(String[] args) {
        if (VIRUSTOTAL_API_KEY == null || VIRUSTOTAL_API_KEY.isEmpty()) {
            System.err.println("❌ ERROR: VirusTotal API Key is not set!");
            return;
        }
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("🚀 Phishing Detector Server Running on Port: " + PORT);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                executor.execute(() -> handleClient(clientSocket));
            }
        } catch (IOException e) {
            System.err.println("❌ Server Error: " + e.getMessage());
        }
    }

    private static void handleClient(Socket clientSocket) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true)) {

            String url = reader.readLine();
            System.out.println("🔍 Checking URL: " + url);

            String jsonResponse;
            if ("GET_HISTORY".equalsIgnoreCase(url)) {
                jsonResponse = new JSONObject().put("history", getHistory()).toString();
            } else if ("CLEAR_HISTORY".equalsIgnoreCase(url)) {
                clearHistory();
                jsonResponse = new JSONObject().put("message", "History Cleared").toString();
            } else {
                boolean isPhishing = checkUrl(url);
                logURL(url, isPhishing ? "PHISHING" : "SAFE");
                jsonResponse = new JSONObject()
                        .put("url", url)
                        .put("status", isPhishing ? "PHISHING" : "SAFE")
                        .toString();
            }

            writer.println(jsonResponse);
            System.out.println("✅ Response Sent: " + jsonResponse);
        } catch (IOException e) {
            System.err.println("⚠️ Client Error: " + e.getMessage());
        }
    }

    private static boolean checkUrl(String url) {
        try {
            System.out.println("🔍 Sending URL to VirusTotal: " + url);

            // Convert URL to Base64 (VirusTotal API requires this)
            String encodedUrl = Base64.getUrlEncoder().withoutPadding().encodeToString(url.getBytes());
            System.out.println("🔗 Encoded URL: " + encodedUrl);

            // Prepare API URL (Use GET request instead of POST)
            URL apiUrl = new URL(VIRUSTOTAL_API_URL + "/" + encodedUrl);
            HttpURLConnection conn = (HttpURLConnection) apiUrl.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("x-apikey", VIRUSTOTAL_API_KEY);

            // Get Response Code
            int responseCode = conn.getResponseCode();
            System.out.println("📡 API Response Code: " + responseCode);

            if (responseCode == 200) {
                // Read the response
                String response = new BufferedReader(new InputStreamReader(conn.getInputStream()))
                        .lines().reduce("", String::concat);
                System.out.println("🔍 VirusTotal API Raw Response: " + response);  // Print full response

                // Parse JSON Response
                JSONObject jsonResponse = new JSONObject(response);
                JSONObject stats = jsonResponse.getJSONObject("data").getJSONObject("attributes").getJSONObject("last_analysis_stats");

                int maliciousCount = stats.getInt("malicious");
                System.out.println("⚠️ Malicious Reports: " + maliciousCount);

                return maliciousCount > 0;  // If any service marks it as malicious, return true
            } else {
                System.err.println("⚠️ API Error: Response Code " + responseCode);
            }
        } catch (Exception e) {
            System.err.println("❌ API Request Failed: " + e.getMessage());
        }
        return false;
    }

    private static void logURL(String url, String result) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(LOG_FILE, true))) {
            pw.println(new Date() + " | " + url + " -> " + result);
        } catch (IOException e) {
            System.err.println("❌ Error logging URL: " + e.getMessage());
        }
    }

    private static String getHistory() {
        StringBuilder history = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(LOG_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                history.append(line).append("\n");
            }
        } catch (IOException e) {
            return "📁 No history available";
        }
        return history.toString();
    }

    private static void clearHistory() {
        try (PrintWriter pw = new PrintWriter(LOG_FILE)) {
            pw.print("");
        } catch (IOException e) {
            System.err.println("❌ Error clearing history: " + e.getMessage());
        }
    }
}
