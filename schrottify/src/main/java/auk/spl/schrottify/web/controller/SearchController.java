package auk.spl.schrottify.web.controller;

import auk.spl.schrottify.web.dto.SearchFormDto;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;

/** Resolves user input to MusicBrainz releases. */
@Controller
public class SearchController {

    /** MusicBrainz result list (FR-2, FR-3). Mode is ALBUM or TRACK. */
    @GetMapping("/search")
    public String search(@ModelAttribute SearchFormDto form,
                         @RequestParam(required = false) String mode) {
        return "search";
    }
}
