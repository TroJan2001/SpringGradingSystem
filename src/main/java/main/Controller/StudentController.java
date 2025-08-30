package main.Controller;

import main.Model.Grade;
import main.Service.GradeService;
import main.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/student")
public class StudentController {

    private final GradeService gradeService;
    private final UserService userService;

    @Autowired
    public StudentController(GradeService gradeService, UserService userService) {
        this.gradeService = gradeService;
        this.userService = userService;
    }

    private Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    private String getUsername() {
        return getAuthentication().getName();
    }

    private String getRole() {
        return getAuthentication().getAuthorities().stream()
                .findFirst()
                .orElseThrow().getAuthority().substring(5);
    }

    @GetMapping
    @Secured("ROLE_student")
    public String getStudentDashboard(Model model) {
        String username = getUsername();
        String role = getRole();
        model.addAttribute("username", username);
        model.addAttribute("role", role);
        return "studentDashboard";
    }

    @PostMapping("/grades")
    @Secured("ROLE_student")
    public String showGrades(Model model) {
        String username = getUsername();
        List<Grade> grades = gradeService.getGradesByStudentName(username);

        if (!userService.userExists(username)) {
            model.addAttribute("resultMessage", "Student not found.");
            return getStudentDashboard(model);
        }

        if (grades.isEmpty()) {
            model.addAttribute("resultMessage", "No grades found.");
        } else {
            model.addAttribute("grades", grades);
        }

        return "showGrades";
    }
}
