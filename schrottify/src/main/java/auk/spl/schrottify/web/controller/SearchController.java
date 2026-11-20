package auk.spl.schrottify.web.controller;

import auk.spl.schrottify.web.dto.SearchFormDto;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

/** Resolves user input to MusicBrainz releases (FR-2, FR-3). */
@Controller
public class SearchController {

    private static final Logger log = LoggerFactory.getLogger(SearchController.class);

    private static final String MB_BASE_URL = "https://musicbrainz.org/ws/2";
    /** NFR-6: meaningful User-Agent. */
    private static final String USER_AGENT = "Schrottify/0.0.1 ( https://github.com/LukasKreiter/Schrottify-Music-Server )";
    /** NFR-6: max. 1 request per second. */
    private static final long MIN_REQUEST_INTERVAL_MS = 1000;
    private static final int RESULT_LIMIT = 25;
    /** Splits "Artist – Album" / "Artist - Track" on a spaced dash. */
    private static final Pattern ARTIST_SEPARATOR = Pattern.compile("\\s+[-–—]\\s+");
    private static final Pattern LUCENE_SPECIAL = Pattern.compile("([+\\-&|!(){}\\[\\]^\"~*?:\\\\/])");

    private final RestClient musicBrainz;
    private final Object rateLimitLock = new Object();
    private long lastRequestAt = 0;

    public SearchController(RestClient.Builder restClientBuilder) {
        this.musicBrainz = restClientBuilder
                .baseUrl(MB_BASE_URL)
                .defaultHeader(HttpHeaders.USER_AGENT, USER_AGENT)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    /** Search mode: whole releases or single recordings. */
    public enum Mode { ALBUM, TRACK }

    /** One MusicBrainz hit as shown in the result list. */
    public record SearchResult(String mbid, String artist, String title, String year,
                               String type, Integer trackCount, int score) {
    }

    /** MusicBrainz result list (FR-2, FR-3). Mode is ALBUM or TRACK. */
    @GetMapping("/search")
    public String search(@RequestParam(required = false) String query,
                         @RequestParam(required = false) String mode,
                         Model model) {
        Mode searchMode = parseMode(mode);
        model.addAttribute("mode", searchMode.name());
        model.addAttribute("query", query);
        model.addAttribute("results", List.of());

        SearchFormDto form;
        try {
            form = new SearchFormDto(query);
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "search";
        }
        model.addAttribute("form", form);

        try {
            model.addAttribute("results", queryMusicBrainz(form.getQuery(), searchMode));
        } catch (RestClientException e) {
            log.warn("MusicBrainz search failed for '{}' ({})", form.getQuery(), searchMode, e);
            model.addAttribute("error", "MusicBrainz ist derzeit nicht erreichbar. Bitte später erneut versuchen.");
        }
        return "search";
    }

    private static Mode parseMode(String mode) {
        if (mode == null || mode.isBlank()) {
            return Mode.ALBUM;
        }
        try {
            return Mode.valueOf(mode.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            return Mode.ALBUM;
        }
    }

    private List<SearchResult> queryMusicBrainz(String input, Mode mode) {
        String entity = mode == Mode.ALBUM ? "release" : "recording";
        String luceneQuery = buildLuceneQuery(input, entity);

        throttle();
        Map<String, Object> body = musicBrainz.get()
                .uri(uri -> uri.path("/" + entity)
                        .queryParam("query", luceneQuery)
                        .queryParam("limit", RESULT_LIMIT)
                        .queryParam("fmt", "json")
                        .build())
                .retrieve()
                .body(new ParameterizedTypeReference<>() { });

        if (body == null) {
            return List.of();
        }
        List<Map<String, Object>> items = listOf(body.get(entity + "s"));
        List<SearchResult> results = new ArrayList<>(items.size());
        for (Map<String, Object> item : items) {
            results.add(mode == Mode.ALBUM ? toReleaseResult(item) : toRecordingResult(item));
        }
        return results;
    }

    /** "Artist – Title" becomes a fielded query, anything else is searched as free text. */
    private static String buildLuceneQuery(String input, String entity) {
        String[] parts = ARTIST_SEPARATOR.split(input, 2);
        if (parts.length == 2 && !parts[0].isBlank() && !parts[1].isBlank()) {
            return "artist:\"" + escape(parts[0].trim()) + "\" AND "
                    + entity + ":\"" + escape(parts[1].trim()) + "\"";
        }
        return escape(input);
    }

    private static String escape(String value) {
        return LUCENE_SPECIAL.matcher(value).replaceAll("\\\\$1");
    }

    private static SearchResult toReleaseResult(Map<String, Object> release) {
        Map<String, Object> group = mapOf(release.get("release-group"));
        return new SearchResult(
                str(release.get("id")),
                artistCredit(release),
                str(release.get("title")),
                year(str(release.get("date"))),
                str(group.get("primary-type")),
                release.get("track-count") instanceof Number n ? n.intValue() : null,
                score(release));
    }

    private static SearchResult toRecordingResult(Map<String, Object> recording) {
        List<Map<String, Object>> releases = listOf(recording.get("releases"));
        String date = str(recording.get("first-release-date"));
        if (date == null && !releases.isEmpty()) {
            date = str(releases.getFirst().get("date"));
        }
        return new SearchResult(
                str(recording.get("id")),
                artistCredit(recording),
                str(recording.get("title")),
                year(date),
                releases.isEmpty() ? null : str(releases.getFirst().get("title")),
                null,
                score(recording));
    }

    /** Joins the artist credit including join phrases ("A feat. B"). */
    private static String artistCredit(Map<String, Object> entity) {
        StringBuilder sb = new StringBuilder();
        for (Map<String, Object> credit : listOf(entity.get("artist-credit"))) {
            String name = str(credit.get("name"));
            if (name == null) {
                name = str(mapOf(credit.get("artist")).get("name"));
            }
            sb.append(name == null ? "" : name);
            String join = str(credit.get("joinphrase"));
            sb.append(join == null ? "" : join);
        }
        return sb.toString();
    }

    private static int score(Map<String, Object> entity) {
        Object score = entity.get("score");
        if (score instanceof Number n) {
            return n.intValue();
        }
        try {
            return score == null ? 0 : Integer.parseInt(score.toString());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private static String year(String date) {
        return date != null && date.length() >= 4 ? date.substring(0, 4) : null;
    }

    private static String str(Object value) {
        return value == null ? null : value.toString();
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> mapOf(Object value) {
        return value instanceof Map<?, ?> m ? (Map<String, Object>) m : Map.of();
    }

    @SuppressWarnings("unchecked")
    private static List<Map<String, Object>> listOf(Object value) {
        if (!(value instanceof List<?> list)) {
            return List.of();
        }
        List<Map<String, Object>> out = new ArrayList<>(list.size());
        for (Object o : list) {
            if (o instanceof Map<?, ?> m) {
                out.add((Map<String, Object>) m);
            }
        }
        return out;
    }

    /** Blocks until at least one second has passed since the previous MusicBrainz call (NFR-6). */
    private void throttle() {
        synchronized (rateLimitLock) {
            long wait = lastRequestAt + MIN_REQUEST_INTERVAL_MS - System.currentTimeMillis();
            if (wait > 0) {
                try {
                    Thread.sleep(wait);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new RestClientException("Interrupted while waiting for MusicBrainz rate limit", e);
                }
            }
            lastRequestAt = System.currentTimeMillis();
        }
    }
}
