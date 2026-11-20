package auk.spl.schrottify.service;

import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Service;

/**
 * Service providing dashboard data and overview metrics.
 */
@Service
public class DashboardService {

    public Map<String, Object> getDashboardData() {
        return new HashMap<>();
    }
}
