package auk.spl.schrottify.web.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
/** Dashboard endpoints will be implemented here. */

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/** Entry pages: dashboard and login. */
@Controller
public class DashboardController {

    /** Search field and active jobs. */
    @GetMapping("/")
    public String dashboard() {
        return "dashboard";
    }

    /** Login form (FR-1). The POST to /login is handled by Spring Security. */
    @GetMapping("/login")
    public String login() {
        return "login";
    }
}
