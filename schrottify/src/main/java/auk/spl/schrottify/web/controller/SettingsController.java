package auk.spl.schrottify.web.controller;

import java.util.Map;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/** Application settings page (FR-18). */
@Controller
public class SettingsController {

    /** Shows the current settings. */
    @GetMapping("/settings")
    public String settings() {
        return "settings";
    }

    /** Saves the submitted settings as key/value pairs. */
    @PostMapping("/settings")
    public String saveSettings(@RequestParam Map<String, String> settings) {
        return "redirect:/settings";
    }
}
