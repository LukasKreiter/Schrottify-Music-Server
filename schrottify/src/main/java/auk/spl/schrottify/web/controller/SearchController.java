package auk.spl.schrottify.web.controller;

import auk.spl.schrottify.service.SearchService;
import auk.spl.schrottify.service.SearchService.Mode;
import auk.spl.schrottify.service.SearchService.SearchResult;
import auk.spl.schrottify.web.dto.SearchFormDto;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestClientException;

/** Resolves user input to MusicBrainz releases (FR-2, FR-3). */
@Controller
public class SearchController {

    private static final Logger log = LoggerFactory.getLogger(SearchController.class);

    private final SearchService searchService;

    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    /** MusicBrainz result list (FR-2, FR-3). Mode is ALBUM or TRACK. */
    @GetMapping("/search")
    public String search(@RequestParam(required = false) String query,
                         @RequestParam(required = false) String mode,
                         Model model) {
        Mode searchMode = searchService.parseMode(mode);
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
            model.addAttribute("results", searchService.search(form.getQuery(), searchMode));
        } catch (RestClientException e) {
            log.warn("MusicBrainz search failed for '{}' ({})", form.getQuery(), searchMode, e);
            model.addAttribute("error", "MusicBrainz ist derzeit nicht erreichbar. Bitte später erneut versuchen.");
        }
        return "search";
    }
}
