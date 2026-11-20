package auk.spl.schrottify.web.controller;

import auk.spl.schrottify.service.SettingsService;
import java.util.Map;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/** Application settings page (FR-18). */
@Controller
public class SettingsController {

    private final SettingsService settingsService;

    public SettingsController(SettingsService settingsService) {
        this.settingsService = settingsService;
    }

    /** Shows the current settings. */
    @GetMapping("/settings")
    public String settings(Model model) {
        model.addAttribute("settings", settingsService.getSettings());
        return "settings";
    }

    /** Saves the submitted settings as key/value pairs. */
    @PostMapping("/settings")
    public String saveSettings(@RequestParam Map<String, String> settings) {
        settingsService.saveSettings(settings);
        return "redirect:/settings";
    }
}
