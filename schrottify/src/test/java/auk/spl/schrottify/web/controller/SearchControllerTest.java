package auk.spl.schrottify.web.controller;

import static org.junit.jupiter.api.Assertions.*;

import auk.spl.schrottify.service.SearchService;
import auk.spl.schrottify.service.SearchService.Mode;
import auk.spl.schrottify.service.SearchService.SearchResult;
import java.io.IOException;
import java.util.List;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ui.ConcurrentModel;
import org.springframework.web.client.RestClient;

class SearchControllerTest {

    private MockWebServer server;
    private SearchController controller;

    @BeforeEach
    void setUp() throws IOException {
        server = new MockWebServer();
        server.start();
        SearchService searchService = new SearchService(RestClient.builder(), server.url("/").toString(), 0);
        controller = new SearchController(searchService);
    }

    @AfterEach
    void tearDown() throws IOException {
        server.shutdown();
    }

    @Test
    void search_emptyQuery_setsError() {
        ConcurrentModel model = new ConcurrentModel();
        String view = controller.search("", "album", model);

        assertEquals("search", view);
        assertTrue(model.containsAttribute("error"));
        assertEquals("ALBUM", model.getAttribute("mode"));
    }

    @Test
    void search_validQuery_populatesResults() {
        String json = """
            {
              "releases": [
                {
                  "id": "rel-1",
                  "title": "Abbey Road",
                  "date": "1969",
                  "score": 100,
                  "track-count": 17,
                  "artist-credit": [{ "name": "The Beatles" }],
                  "release-group": { "primary-type": "Album" }
                }
              ]
            }
            """;
        server.enqueue(new MockResponse()
                .setHeader("Content-Type", "application/json")
                .setBody(json));

        ConcurrentModel model = new ConcurrentModel();
        String view = controller.search("The Beatles - Abbey Road", "album", model);

        assertEquals("search", view);
        @SuppressWarnings("unchecked")
        List<SearchResult> results = (List<SearchResult>) model.getAttribute("results");
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals("Abbey Road", results.getFirst().title());
    }

    @Test
    void search_serverError_setsErrorMessage() {
        server.enqueue(new MockResponse().setResponseCode(500));

        ConcurrentModel model = new ConcurrentModel();
        String view = controller.search("Beatles", "album", model);

        assertEquals("search", view);
        assertTrue(model.containsAttribute("error"));
    }
}
