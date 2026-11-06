package auk.spl.schrottify.SubsonicAPI;

import org.junit.jupiter.api.Test;
import java.net.URI;
import java.net.http.*;
import java.security.MessageDigest;
import java.util.UUID;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;



public class APICallTest {

    public class SubsonicClient {
        private final String baseUrl, user, pass;

        public SubsonicClient(String baseUrl, String user, String pass) {
            this.baseUrl = baseUrl;
            this.user = user;
            this.pass = pass;
        }

        public String ping() throws Exception {
            String salt = UUID.randomUUID().toString().substring(0, 12);
            String token = md5(pass + salt);
            String url = baseUrl + "/rest/ping.view"
                    + "?u=" + user + "&t=" + token + "&s=" + salt
                    + "&v=1.15.0&c=Test&f=json";
            var req = HttpRequest.newBuilder(URI.create(url)).GET().build();
            return HttpClient.newHttpClient().send(req, HttpResponse.BodyHandlers.ofString()).body();
        }

        private static String md5(String s) throws Exception {
            var md = MessageDigest.getInstance("MD5");
            var bytes = md.digest(s.getBytes());
            var sb = new StringBuilder();
            for (byte b : bytes) sb.append(String.format("%02x", b));
            return sb.toString();
        }
    }


    @Nested
    class SubsonicClientTest {

        private static final String BASE_URL = "http://100.100.179.92:4533"; // ← your server
        private static final String USER     = "Schrottify";
        private static final String PASS     = "HieblerStinkt";

        @Test
        void ping() throws Exception {
            var client = new SubsonicClient(BASE_URL, USER, PASS);
            String response = client.ping();
            assertTrue(response.contains("\"status\":\"ok\""), "Got: " + response);
        }
    }
}
