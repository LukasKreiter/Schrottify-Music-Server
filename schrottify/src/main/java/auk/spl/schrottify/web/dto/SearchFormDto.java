package auk.spl.schrottify.web.dto;

/**
 * Data transfer object for search form submissions.
 * Immutable record containing the search query.
 */
public record SearchFormDto(String query) {
    /**
     * Validates the search form data.
     * @throws IllegalArgumentException if the query is null or empty after trimming.
     */
    public SearchFormDto {
        if (query == null || query.trim().isEmpty()) {
            throw new IllegalArgumentException("Search query cannot be empty");
        }
    }

    /**
     * Gets the search query, trimmed of leading/trailing whitespace.
     * @return The trimmed search query.
     */
    public String getQuery() {
        return query.trim();
    }
}