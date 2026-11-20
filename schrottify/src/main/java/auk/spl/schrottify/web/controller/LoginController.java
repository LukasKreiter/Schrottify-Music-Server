package auk.spl.schrottify.web.controller;

import auk.spl.schrottify.service.LoginService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

    private final LoginService loginService;

    public LoginController(LoginService loginService) {
        this.loginService = loginService;
    }

    @GetMapping("/login")
    public String login(
            @RequestParam(name = "error", required = false) String error,
            @RequestParam(name = "logout", required = false) String logout,
            Model model) {
        var viewData = loginService.getLoginViewData(error, logout);
        model.addAttribute("loginError", viewData.loginError());
        model.addAttribute("loggedOut", viewData.loggedOut());
        return "login";
    }
}
