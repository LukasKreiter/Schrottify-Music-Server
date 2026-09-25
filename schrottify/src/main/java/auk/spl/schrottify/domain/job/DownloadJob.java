package auk.spl.schrottify.domain.job;

import java.util.UUID;

public record DownloadJob(UUID id, JobStatus status) {
}
