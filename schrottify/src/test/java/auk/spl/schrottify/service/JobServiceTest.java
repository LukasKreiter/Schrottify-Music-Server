package auk.spl.schrottify.service;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class JobServiceTest {

    private final JobService jobService = new JobService();

    @Test
    void jobOperationsRunWithoutException() {
        assertDoesNotThrow(() -> jobService.createJob("query", "ALBUM", "mb-1"));
        assertNotNull(jobService.listJobs(null, null, null, null, 0));
        assertNotNull(jobService.getActiveJobs());
        assertNotNull(jobService.getJobDetail(UUID.randomUUID()));
        assertDoesNotThrow(() -> jobService.cancelJob(UUID.randomUUID()));
        assertDoesNotThrow(() -> jobService.retryJob(UUID.randomUUID()));
        assertDoesNotThrow(() -> jobService.deleteJob(UUID.randomUUID()));
        assertDoesNotThrow(() -> jobService.uploadFiles(List.of()));
    }
}
