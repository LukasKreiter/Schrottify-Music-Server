package auk.spl.schrottify.SubsonicAPI;

import auk.spl.schrottify.SubsonicAPI.TestStubs.SubsonicClientStub;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration test against the public Navidrome demo server.
 * Run with: mvn test -Dgroups=integration
 * Exclude from normal runs: mvn test -Dgroups="!integration"
 */
@Tag("integration")
class SubsonicClientIntegrationTest {

    private static final String BASE_URL = "http://100.100.179.92:4533";
    private static final String USERNAME = "Schrottify";
    private static final String PASSWORD = "HieblerStinkt";

    private SubsonicClientStub client;

    @BeforeEach
    void setUp() {
        client = new SubsonicClientStub(BASE_URL, USERNAME, PASSWORD);
    }

    // ─── Ping ─────────────────────────────────────────────────────

    @Test
    void ping_returnsOk() throws Exception {
        String response = client.request("ping");
        assertTrue(response.contains("\"status\":\"ok\""),
                "Expected ok status, got: " + response);
    }

    @Test
    void ping_returnsVersion() throws Exception {
        String response = client.request("ping");
        assertTrue(response.contains("\"version\""),
                "Expected version in response, got: " + response);
    }

    // ─── Browse ───────────────────────────────────────────────────

    @Test
    void getIndexes_returnsArtistList() throws Exception {
        String response = client.request("getIndexes");
        assertTrue(response.contains("\"index\""),
                "Expected index list, got: " + response);
        assertTrue(response.contains("\"name\""),
                "Expected artist names, got: " + response);
    }

    @Test
    void getMusicDirectory_returnsChildren() throws Exception {
        // Get root music directory
        String response = client.request("getMusicDirectory", "id=0");
        assertTrue(response.contains("\"child\""),
                "Expected child entries, got: " + response);
    }

    // ─── Search ───────────────────────────────────────────────────

    @Test
    void search3_findsSongs() throws Exception {
        String response = client.request("search3",
                "query=Nine Inch Nails",
                "songCount=5");
        assertTrue(response.contains("\"song\""),
                "Expected songs in search result, got: " + response);
    }

    @Test
    void search3_artistSearch() throws Exception {
        String response = client.request("search3",
                "query=NIN",
                "artistCount=3");
        assertTrue(response.contains("\"artist\""),
                "Expected artists in search result, got: " + response);
    }

    // ─── Random songs ─────────────────────────────────────────────

    @Test
    void getRandomSongs_returnsSongs() throws Exception {
        String response = client.request("getRandomSongs", "size=5");
        assertTrue(response.contains("\"song\""),
                "Expected random songs, got: " + response);
    }

    // ─── Top songs / albums ───────────────────────────────────────

    @Test
    void getTopSongs_returnsList() throws Exception {
        String response = client.request("getTopSongs", "size=10");
        assertTrue(response.contains("\"song\""),
                "Expected top songs, got: " + response);
    }

    @Test
    void getNowPlaying_returnsList() throws Exception {
        // May be empty on a demo server, but should not error
        String response = client.request("getNowPlaying");
        assertTrue(response.contains("\"status\":\"ok\""),
                "Expected ok status, got: " + response);
    }

    // ─── Stream URL (don't actually download) ─────────────────────

    @Test
    void getSong_returnsMetadata() throws Exception {
        // First grab a song ID from random songs
        String randomResponse = client.request("getRandomSongs", "size=1");
        // Extract first song id (simple regex for "id":"...")
        String id = extractFirstId(randomResponse);
        assertNotNull(id, "Could not extract song id from: " + randomResponse);

        String response = client.request("getSong", "id=" + id);
        assertTrue(response.contains("\"song\""),
                "Expected song metadata, got: " + response);
        assertTrue(response.contains("streamUrl") || response.contains("id"),
                "Expected stream info, got: " + response);
    }

    // ─── Error handling ───────────────────────────────────────────

    @Test
    void badCredentials_returnsError() throws Exception {
        SubsonicClientStub badClient = new SubsonicClientStub(BASE_URL, "demo", "wrongpass");
        String response = badClient.request("ping");
        assertTrue(response.contains("\"error\""),
                "Expected error response, got: " + response);
    }

    // ─── Helper ───────────────────────────────────────────────────

    private static String extractFirstId(String json) {
        int idx = json.indexOf("\"id\":\"");
        if (idx == -1) return null;
        int start = idx + 6;
        int end = json.indexOf("\"", start);
        return end == -1 ? null : json.substring(start, end);
    }
}   