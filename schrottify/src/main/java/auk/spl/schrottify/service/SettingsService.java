package auk.spl.schrottify.service;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;

/**
 * Service managing application settings (FR-18).
 */
@Service
public class SettingsService {

    private final Map<String, String> settings = new ConcurrentHashMap<>();

    public Map<String, String> getSettings() {
        return Collections.unmodifiableMap(settings);
    }

    public void saveSettings(Map<String, String> newSettings) {
        if (newSettings != null) {
            settings.putAll(newSettings);
        }
    }
}
