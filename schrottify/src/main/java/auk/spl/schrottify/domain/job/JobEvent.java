package auk.spl.schrottify.domain.job;

import java.time.Instant;

public record JobEvent(Instant at, String message) {
}
