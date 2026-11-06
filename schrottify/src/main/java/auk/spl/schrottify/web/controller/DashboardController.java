package auk.spl.schrottify.web.controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
/** Dashboard endpoints will be implemented here. */
@RestController
@RequestMapping("/api/dashboard/login")
public class DashboardController {

    @GetMapping
    public String login() {
        return null;
    }
}
