package auk.spl.schrottify.service;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class LoginServiceTest {

    private final LoginService loginService = new LoginService();

    @Test
    void getLoginViewData_noParams() {
        var data = loginService.getLoginViewData(null, null);
        assertFalse(data.loginError());
        assertFalse(data.loggedOut());
    }

    @Test
    void getLoginViewData_withError() {
        var data = loginService.getLoginViewData("true", null);
        assertTrue(data.loginError());
        assertFalse(data.loggedOut());
    }

    @Test
    void getLoginViewData_withLogout() {
        var data = loginService.getLoginViewData(null, "true");
        assertFalse(data.loginError());
        assertTrue(data.loggedOut());
    }

    @Test
    void getLoginViewData_withBoth() {
        var data = loginService.getLoginViewData("true", "true");
        assertTrue(data.loginError());
        assertTrue(data.loggedOut());
    }
}
