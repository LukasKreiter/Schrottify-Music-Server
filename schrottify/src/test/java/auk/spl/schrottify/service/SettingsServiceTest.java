package auk.spl.schrottify.service;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;
import org.junit.jupiter.api.Test;

class SettingsServiceTest {

    private final SettingsService settingsService = new SettingsService();

    @Test
    void getAndSaveSettings() {
        assertTrue(settingsService.getSettings().isEmpty());

        settingsService.saveSettings(Map.of("key1", "val1", "key2", "val2"));
        assertEquals("val1", settingsService.getSettings().get("key1"));
        assertEquals("val2", settingsService.getSettings().get("key2"));

        settingsService.saveSettings(null);
        assertEquals(2, settingsService.getSettings().size());
    }
}
