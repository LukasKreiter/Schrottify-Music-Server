package auk.spl.schrottify.navidrome;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ScanTrigger {

    // TODO: Add credentials to environment variables and read from there.

    private static final String API_URL = "http://[IP_ADDRESS]/api/v1/forceScan?scanAll=true&rescanAll=false";
    private static final String USER = "admin";
    private static final String PASS = "navidrome"; // TODO: Hardcoded fallback

    public static void triggerScan() {
        try {
            var client = HttpClient.newHttpClient();
            var request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL))
                    .header("Accept", "application/json")
                    .header("Authorization", getBasicAuthHeader())
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .build();

            var response = client.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println("Status: " + response.statusCode());
            System.out.println("Body: " + response.body());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String getBasicAuthHeader() {
        String auth = USER + ":" + PASS;
        return "Basic " + java.util.Base64.getEncoder().encodeToString(auth.getBytes());
    }

    public static void main(String[] args) {
        triggerScan();
    }
}
