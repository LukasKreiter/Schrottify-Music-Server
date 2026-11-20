package auk.spl.schrottify.web.controller;

import static org.junit.jupiter.api.Assertions.*;

import auk.spl.schrottify.service.LoginService;
import org.junit.jupiter.api.Test;
import org.springframework.ui.ConcurrentModel;

class LoginControllerTest {

    private final LoginService loginService = new LoginService();
    private final LoginController loginController = new LoginController(loginService);

    @Test
    void login_populatesModelAttributes() {
        ConcurrentModel model = new ConcurrentModel();
        String view = loginController.login("true", null, model);

        assertEquals("login", view);
        assertEquals(true, model.getAttribute("loginError"));
        assertEquals(false, model.getAttribute("loggedOut"));
    }

    @Test
    void login_loggedOutAttribute() {
        ConcurrentModel model = new ConcurrentModel();
        String view = loginController.login(null, "true", model);

        assertEquals("login", view);
        assertEquals(false, model.getAttribute("loginError"));
        assertEquals(true, model.getAttribute("loggedOut"));
    }
}
