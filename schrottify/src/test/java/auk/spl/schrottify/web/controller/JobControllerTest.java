package auk.spl.schrottify.web.controller;

import static org.junit.jupiter.api.Assertions.*;

import auk.spl.schrottify.service.JobService;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.ui.ConcurrentModel;

class JobControllerTest {

    private final JobService jobService = new JobService();
    private final JobController jobController = new JobController(jobService);

    @Test
    void createJob_redirects() {
        String view = jobController.createJob("query", "ALBUM", "mb-1");
        assertEquals("redirect:/jobs", view);
    }

    @Test
    void listJobs_returnsListView() {
        ConcurrentModel model = new ConcurrentModel();
        String view = jobController.listJobs(null, null, LocalDate.now(), LocalDate.now(), 0, model);
        assertEquals("jobs/list", view);
        assertTrue(model.containsAttribute("jobs"));
    }

    @Test
    void activeJobs_returnsFragment() {
        ConcurrentModel model = new ConcurrentModel();
        String view = jobController.activeJobs(model);
        assertEquals("jobs/active :: active", view);
        assertTrue(model.containsAttribute("activeJobs"));
    }

    @Test
    void jobDetail_returnsDetailView() {
        UUID id = UUID.randomUUID();
        ConcurrentModel model = new ConcurrentModel();
        String view = jobController.jobDetail(id, model);
        assertEquals("jobs/detail", view);
        assertTrue(model.containsAttribute("job"));
    }

    @Test
    void cancelRetryDelete_redirectCorrectly() {
        UUID id = UUID.randomUUID();
        assertEquals("redirect:/jobs/" + id, jobController.cancelJob(id));
        assertEquals("redirect:/jobs/" + id, jobController.retryJob(id));
        assertEquals("redirect:/jobs", jobController.deleteJob(id));
    }

    @Test
    void upload_returnsCorrectViews() {
        assertEquals("upload", jobController.uploadForm());
        assertEquals("redirect:/jobs", jobController.upload(List.of()));
    }
}
