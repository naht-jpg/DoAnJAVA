package ecourse.secutiry;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import ecourse.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomLoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Autowired
    private UserService userService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {

        // Reset so lan sai ve 0 khi dang nhap thanh cong
        String email = authentication.getName();
        userService.resetFailedAttempts(email);

        // Chuyen huong theo vai tro
        if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("user"))) {
            response.sendRedirect("/home/index");
        } else {
            response.sendRedirect("/admin");
        }
    }
}
