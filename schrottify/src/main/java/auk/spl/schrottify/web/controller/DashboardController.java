package auk.spl.schrottify.web.controller;

import auk.spl.schrottify.service.DashboardService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/** Entry pages: dashboard. */
@Controller
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    /** Search field and active jobs. */
    @GetMapping("/")
    public String dashboard(Model model) {
        model.addAllAttributes(dashboardService.getDashboardData());
        return "dashboard";
    }

    /** Login form (FR-1). Note: The GET /login mapping is handled by LoginController. */
    public String login() {
        return "login";
    }
}
