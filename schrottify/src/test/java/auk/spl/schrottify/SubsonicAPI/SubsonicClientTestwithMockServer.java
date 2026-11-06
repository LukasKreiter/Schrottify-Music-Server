package auk.spl.schrottify.SubsonicAPI;

import auk.spl.schrottify.SubsonicAPI.TestStubs.SubsonicClientStub;
import okhttp3.mockwebserver.*;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.security.MessageDigest;

import static org.junit.jupiter.api.Assertions.*;

class SubsonicClientTestwithMockServer {

    private MockWebServer server;
    private SubsonicClientStub client;

    @BeforeEach
    void setUp() throws IOException {
        server = new MockWebServer();
        server.start();
        client = new SubsonicClientStub(
                server.url("/").toString(), "admin", "admin");
    }

    @AfterEach
    void tearDown() throws IOException {
        server.shutdown();
    }

    // ─── md5 unit tests ───────────────────────────────────────────

    @Test
    void md5_knownInput() throws Exception {
        // "hello" → 5d41402abc4b2a76b9719d911017c592
        String result = client.md5("hello");
        assertEquals("5d41402abc4b2a76b9719d911017c592", result);
    }

    @Test
    void md5_emptyString() throws Exception {
        String result = client.md5("");
        assertEquals("d41d8cd98f00b204e9800998ecf8427e", result);
    }

    @Test
    void md5_is32HexChars() throws Exception {
        String result = client.md5("any input");
        assertTrue(result.matches("^[0-9a-f]{32}$"));
    }

    // ─── request() tests ──────────────────────────────────────────

    @Test
    void ping_returnsSuccess() throws Exception {
        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type", "application/json")
                .setBody("{\"status\":\"ok\",\"version\":\"1.15.0\"}"));

        String response = client.request("ping");

        assertTrue(response.contains("\"status\":\"ok\""));
    }

    @Test
    void getIndexes_returnsJson() throws Exception {
        String json = "{\"indexes\":{\"index\":[{\"name\":\"A\",\"artist\":[]}]}}";
        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type", "application/json")
                .setBody(json));

        String response = client.request("getIndexes");

        assertTrue(response.contains("\"index\""));
    }

    @Test
    void request_sendsCorrectPath() throws Exception {
        server.enqueue(new MockResponse().setResponseCode(200).setBody("{}"));

        client.request("getMusicDirectory", "id=123");

        RecordedRequest recorded = server.takeRequest();
        assertTrue(recorded.getPath().startsWith("/rest/getMusicDirectory.view"));
        assertTrue(recorded.getPath().contains("id=123"));
    }

    @Test
    void request_includesAuthParams() throws Exception {
        server.enqueue(new MockResponse().setResponseCode(200).setBody("{}"));

        client.request("ping");

        RecordedRequest recorded = server.takeRequest();
        String path = recorded.getPath();
        assertTrue(path.contains("u=admin"));
        assertTrue(path.contains("t="));   // token present
        assertTrue(path.contains("s="));   // salt present
        assertTrue(path.contains("v=1.15.0"));
        assertTrue(path.contains("c=MyJavaClient"));
        assertTrue(path.contains("f=json"));
    }

    @Test
    void request_tokenIsCorrectMd5() throws Exception {
        server.enqueue(new MockResponse().setResponseCode(200).setBody("{}"));

        client.request("ping");

        RecordedRequest recorded = server.takeRequest();
        String path = recorded.getPath();

        // Extract salt and token from the query string
        String salt = extractParam(path, "s");
        String token = extractParam(path, "t");

        // Verify token == md5(password + salt)
        String expected = md5("admin" + salt);
        assertEquals(expected, token);
    }

    @Test
    void request_401_returnsErrorBody() throws Exception {
        server.enqueue(new MockResponse()
                .setResponseCode(401)
                .setBody("{\"error\":\"Invalid credentials\"}"));

        String response = client.request("ping");

        assertTrue(response.contains("Invalid credentials"));
    }

    @Test
    void request_500_returnsErrorBody() throws Exception {
        server.enqueue(new MockResponse()
                .setResponseCode(500)
                .setBody("{\"error\":\"Internal Server Error\"}"));

        String response = client.request("getIndexes");

        assertTrue(response.contains("Internal Server Error"));
    }

    // ─── helper ───────────────────────────────────────────────────

    private static String extractParam(String url, String key) {
        String prefix = key + "=";
        int start = url.indexOf(prefix) + prefix.length();
        int end = url.indexOf('&', start);
        return end == -1 ? url.substring(start) : url.substring(start, end);
    }

    private static String md5(String input) throws Exception {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] digest = md.digest(input.getBytes("UTF-8"));
        StringBuilder sb = new StringBuilder();
        for (byte b : digest) sb.append(String.format("%02x", b));
        return sb.toString();
    }
}   