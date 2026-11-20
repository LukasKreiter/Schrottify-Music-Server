package auk.spl.schrottify.service;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class DashboardServiceTest {

    private final DashboardService dashboardService = new DashboardService();

    @Test
    void getDashboardData() {
        assertNotNull(dashboardService.getDashboardData());
    }
}
