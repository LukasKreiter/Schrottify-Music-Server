package auk.spl.schrottify.service;

import static org.junit.jupiter.api.Assertions.*;

import auk.spl.schrottify.service.SearchService.Mode;
import auk.spl.schrottify.service.SearchService.SearchResult;
import java.io.IOException;
import java.util.List;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

class SearchServiceTest {

    private MockWebServer server;
    private SearchService searchService;

    @BeforeEach
    void setUp() throws IOException {
        server = new MockWebServer();
        server.start();
        searchService = new SearchService(RestClient.builder(), server.url("/").toString(), 0);
    }

    @AfterEach
    void tearDown() throws IOException {
        server.shutdown();
    }

    @Test
    void parseMode_defaultsToAlbum() {
        assertEquals(Mode.ALBUM, searchService.parseMode(null));
        assertEquals(Mode.ALBUM, searchService.parseMode(""));
        assertEquals(Mode.ALBUM, searchService.parseMode("unknown"));
    }

    @Test
    void parseMode_parsesCorrectly() {
        assertEquals(Mode.ALBUM, searchService.parseMode("album"));
        assertEquals(Mode.TRACK, searchService.parseMode("track"));
        assertEquals(Mode.TRACK, searchService.parseMode("TRACK"));
    }

    @Test
    void buildLuceneQuery_splitsArtistAndTitle() {
        String query = SearchService.buildLuceneQuery("Pink Floyd - The Wall", "release");
        assertEquals("artist:\"Pink Floyd\" AND release:\"The Wall\"", query);
    }

    @Test
    void buildLuceneQuery_handlesSimpleInput() {
        String query = SearchService.buildLuceneQuery("The Wall", "release");
        assertEquals("The Wall", query);
    }

    @Test
    void search_releaseSuccess() {
        String json = """
            {
              "releases": [
                {
                  "id": "12345",
                  "title": "The Dark Side of the Moon",
                  "date": "1973-03-01",
                  "score": 100,
                  "track-count": 10,
                  "artist-credit": [
                    { "name": "Pink Floyd" }
                  ],
                  "release-group": {
                    "primary-type": "Album"
                  }
                }
              ]
            }
            """;
        server.enqueue(new MockResponse()
                .setHeader("Content-Type", "application/json")
                .setBody(json));

        List<SearchResult> results = searchService.search("Pink Floyd - The Dark Side of the Moon", Mode.ALBUM);

        assertEquals(1, results.size());
        SearchResult result = results.getFirst();
        assertEquals("12345", result.mbid());
        assertEquals("Pink Floyd", result.artist());
        assertEquals("The Dark Side of the Moon", result.title());
        assertEquals("1973", result.year());
        assertEquals("Album", result.type());
        assertEquals(10, result.trackCount());
        assertEquals(100, result.score());
    }

    @Test
    void search_recordingSuccess() {
        String json = """
            {
              "recordings": [
                {
                  "id": "rec-1",
                  "title": "Time",
                  "score": 95,
                  "first-release-date": "1973-03-01",
                  "artist-credit": [
                    { "name": "Pink Floyd" }
                  ],
                  "releases": [
                    { "title": "The Dark Side of the Moon", "date": "1973" }
                  ]
                }
              ]
            }
            """;
        server.enqueue(new MockResponse()
                .setHeader("Content-Type", "application/json")
                .setBody(json));

        List<SearchResult> results = searchService.search("Pink Floyd - Time", Mode.TRACK);

        assertEquals(1, results.size());
        SearchResult result = results.getFirst();
        assertEquals("rec-1", result.mbid());
        assertEquals("Pink Floyd", result.artist());
        assertEquals("Time", result.title());
        assertEquals("1973", result.year());
        assertEquals("The Dark Side of the Moon", result.type());
        assertEquals(95, result.score());
    }
}
