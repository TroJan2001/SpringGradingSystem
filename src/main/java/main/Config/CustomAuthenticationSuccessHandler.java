package main.Config;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        String roleWithPrefix = authentication.getAuthorities().iterator().next().getAuthority();
        switch (roleWithPrefix) {
            case "ROLE_admin":
            case "ROLE_super_admin":
                response.sendRedirect("/admin");
                break;
            case "ROLE_teacher":
                response.sendRedirect("/teacher");
                break;
            case "ROLE_student":
                response.sendRedirect("/student");
                break;
            default:
                response.sendRedirect("/login");
        }
    }
}
