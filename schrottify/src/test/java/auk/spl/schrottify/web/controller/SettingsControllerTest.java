package auk.spl.schrottify.web.controller;

import static org.junit.jupiter.api.Assertions.*;

import auk.spl.schrottify.service.SettingsService;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.ui.ConcurrentModel;

class SettingsControllerTest {

    private final SettingsService settingsService = new SettingsService();
    private final SettingsController controller = new SettingsController(settingsService);

    @Test
    void settings_populatesModel() {
        ConcurrentModel model = new ConcurrentModel();
        String view = controller.settings(model);
        assertEquals("settings", view);
        assertTrue(model.containsAttribute("settings"));
    }

    @Test
    void saveSettings_redirectsAndPersists() {
        String view = controller.saveSettings(Map.of("theme", "dark"));
        assertEquals("redirect:/settings", view);
        assertEquals("dark", settingsService.getSettings().get("theme"));
    }
}
