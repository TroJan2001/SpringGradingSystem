package main.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequestMapping("/login")
public class LoginController {

    private final AuthenticationManager authenticationManager;

    @Autowired
    public LoginController(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    private static final Map<String, String> ROLE_PATHS = Map.of(
            "ROLE_admin", "redirect:/admin",
            "ROLE_super_admin", "redirect:/admin",
            "ROLE_teacher", "redirect:/teacher",
            "ROLE_student", "redirect:/student"
    );

    private static final String LOGIN_VIEW = "login";

    @GetMapping
    public String login() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !authentication.getAuthorities().isEmpty()) {
            String role = authentication.getAuthorities().stream()
                    .findFirst()
                    .orElseThrow().getAuthority();
            return ROLE_PATHS.getOrDefault(role, LOGIN_VIEW);
        }
        return LOGIN_VIEW;
    }

    @PostMapping
    public String login(@RequestParam("username") String username,
                        @RequestParam("password") String password) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String role = authentication.getAuthorities().stream()
                    .findFirst()
                    .orElseThrow().getAuthority().substring(5);
            return ROLE_PATHS.getOrDefault(role, LOGIN_VIEW);
        } catch (Exception e) {
            return "login";
        }
    }

    @ExceptionHandler(IllegalStateException.class)
    public String handleIllegalStateException() {
        return login();
    }
}
