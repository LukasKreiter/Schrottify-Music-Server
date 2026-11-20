package auk.spl.schrottify.service;

import org.springframework.stereotype.Service;

/**
 * Service handling login view state and validation logic (FR-1).
 */
@Service
public class LoginService {

    public record LoginViewData(boolean loginError, boolean loggedOut) {
    }

    public LoginViewData getLoginViewData(String error, String logout) {
        return new LoginViewData(isLoginError(error), isLoggedOut(logout));
    }

    public boolean isLoginError(String error) {
        return error != null;
    }

    public boolean isLoggedOut(String logout) {
        return logout != null;
    }
}
