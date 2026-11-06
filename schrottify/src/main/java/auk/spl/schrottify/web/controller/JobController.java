package auk.spl.schrottify.web.controller;

import auk.spl.schrottify.domain.job.JobStatus;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

/** Job lifecycle: creation, history, detail, actions and file upload. */
@Controller
public class JobController {

    /** Starts a download job from a search result ("Auf Server laden"). */
    @PostMapping("/jobs")
    public String createJob(@RequestParam String query,
                            @RequestParam String mode,
                            @RequestParam(required = false) String mbReleaseId) {
        return "redirect:/jobs";
    }

    /** Job history with filter, search and pagination (FR-14). */
    @GetMapping("/jobs")
    public String listJobs(@RequestParam(required = false) JobStatus status,
                           @RequestParam(required = false) String query,
                           @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
                           @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
                           @RequestParam(defaultValue = "0") int page) {
        return "jobs/list";
    }

    /** HTMX fragment with the active jobs, polled every 2s (FR-9). */
    @GetMapping("/jobs/active")
    public String activeJobs() {
        return "jobs/active :: active";
    }

    /** Candidates, files, progress and event log of a job (FR-14). */
    @GetMapping("/jobs/{id}")
    public String jobDetail(@PathVariable UUID id) {
        return "jobs/detail";
    }

    /** Cancels a job (FR-13). */
    @PostMapping("/jobs/{id}/cancel")
    public String cancelJob(@PathVariable UUID id) {
        return "redirect:/jobs/" + id;
    }

    /** Retries a job (FR-13). */
    @PostMapping("/jobs/{id}/retry")
    public String retryJob(@PathVariable UUID id) {
        return "redirect:/jobs/" + id;
    }

    /** Deletes a job (FR-13). */
    @PostMapping("/jobs/{id}/delete")
    public String deleteJob(@PathVariable UUID id) {
        return "redirect:/jobs";
    }

    /** Upload form for own files (FR-15). */
    @GetMapping("/upload")
    public String uploadForm() {
        return "upload";
    }

    /** Feeds uploaded files into the import pipeline (FR-15). */
    @PostMapping("/upload")
    public String upload(@RequestParam List<MultipartFile> files) {
        return "redirect:/jobs";
    }
}
