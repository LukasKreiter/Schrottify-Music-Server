package auk.spl.schrottify.web.controller;

import static org.junit.jupiter.api.Assertions.*;

import auk.spl.schrottify.service.DashboardService;
import org.junit.jupiter.api.Test;
import org.springframework.ui.ConcurrentModel;

class DashboardControllerTest {

    private final DashboardService dashboardService = new DashboardService();
    private final DashboardController controller = new DashboardController(dashboardService);

    @Test
    void dashboard_returnsDashboardView() {
        ConcurrentModel model = new ConcurrentModel();
        String view = controller.dashboard(model);
        assertEquals("dashboard", view);
    }

    @Test
    void login_returnsLoginView() {
        assertEquals("login", controller.login());
    }
}
