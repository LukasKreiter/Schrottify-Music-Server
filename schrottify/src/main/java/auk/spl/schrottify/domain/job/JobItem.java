package auk.spl.schrottify.domain.job;

import java.util.UUID;

public record JobItem(UUID id, UUID jobId, String title) {
}
