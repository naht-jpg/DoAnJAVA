package ecourse.secutiry;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import ecourse.model.UserClass;
import ecourse.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomLoginFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @Autowired
    private UserService userService;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException exception) throws IOException, ServletException {

        String email = request.getParameter("username");
        UserClass user = userService.findByUsername(email);

        if (user != null) {
            if (user.isAccountNonLocked()) {
                if (user.getFailedAttempt() < UserService.MAX_FAILED_ATTEMPTS - 1) {
                    userService.increaseFailedAttempts(user);
                    int remaining = UserService.MAX_FAILED_ATTEMPTS - user.getFailedAttempt() - 1;
                    exception = new LockedException(
                            "Sai email hoac mat khau. Con " + remaining + " lan thu.");
                } else {
                    userService.increaseFailedAttempts(user);
                    exception = new LockedException(
                            "Tai khoan da bi khoa do nhap sai " + UserService.MAX_FAILED_ATTEMPTS
                                    + " lan. Vui long thu lai sau 1 phut.");
                }
            } else {
                if (userService.unlockWhenTimeExpired(user)) {
                    exception = new LockedException(
                            "Tai khoan da duoc mo khoa. Vui long dang nhap lai.");
                } else {
                    exception = new LockedException(
                            "Tai khoan dang bi khoa. Vui long thu lai sau 1 phut.");
                }
            }
        }

        super.setDefaultFailureUrl("/home/signin?error");
        super.onAuthenticationFailure(request, response, exception);
    }
}
