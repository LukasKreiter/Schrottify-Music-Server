package auk.spl.schrottify.support;

/** Utility class for creating URL-safe slugs from strings. */
public final class Slugify {
    private Slugify() {
    }

    /**
     * Converts a string to a URL-safe slug.
     * Replaces spaces and special characters with hyphens and converts to
     * lowercase.
     * 
     * @param input The string to convert.
     * @return The slugified string.
     */
    public static String slugify(String input) {
        if (input == null) {
            return "";
        }
        return input
                .toLowerCase()
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("^-|-$", ""); // Remove leading/trailing hyphens
    }
}