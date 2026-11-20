package auk.spl.schrottify.service;

import auk.spl.schrottify.domain.job.JobStatus;
import auk.spl.schrottify.web.dto.JobViewDto;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * Service managing job lifecycle operations (FR-9, FR-13, FR-14, FR-15).
 */
@Service
public class JobService {

    public void createJob(String query, String mode, String mbReleaseId) {
        // Starts a download job from a search result
    }

    public List<JobViewDto> listJobs(JobStatus status, String query, LocalDate from, LocalDate to, int page) {
        return List.of();
    }

    public List<JobViewDto> getActiveJobs() {
        return List.of();
    }

    public JobViewDto getJobDetail(UUID id) {
        return new JobViewDto(id.toString(), JobStatus.QUEUED.name());
    }

    public void cancelJob(UUID id) {
        // Cancels a job
    }

    public void retryJob(UUID id) {
        // Retries a job
    }

    public void deleteJob(UUID id) {
        // Deletes a job
    }

    public void uploadFiles(List<MultipartFile> files) {
        // Feeds uploaded files into the import pipeline
    }
}
