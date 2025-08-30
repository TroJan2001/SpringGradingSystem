package main.Controller;

import main.Model.Course;
import main.Model.Grade;
import main.Service.CourseService;
import main.Service.GradeService;
import main.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/teacher")
public class TeacherController {

    private final GradeService gradeService;
    private final CourseService courseService;
    private final UserService userService;

    @Autowired
    public TeacherController(GradeService gradeService, CourseService courseService, UserService userService) {
        this.gradeService = gradeService;
        this.courseService = courseService;
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
    @Secured("ROLE_teacher")
    public String getTeacherDashboard(Model model) {
        model.addAttribute("username", getUsername());
        model.addAttribute("role", getRole());
        return "teacherDashboard";
    }

    @PostMapping("/courseGrades")
    @Secured("ROLE_teacher")
    public String getCourseGrades(@RequestParam("courseName") String courseName,
                                  Model model) {
        String username = getUsername();

        if (!isCourseAuthorized(courseName, username)) {
            model.addAttribute("resultMessage", "Unauthorized or course does not exist.");
            return getTeacherDashboard(model);
        }

        List<Grade> grades = gradeService.getCourseGrades(courseName);
        if (grades.isEmpty()) {
            model.addAttribute("resultMessage", "No grades found for this course.");
        } else {
            double averageGrade = grades.stream().mapToDouble(Grade::getGrade).average().orElse(0);
            double minGrade = grades.stream().mapToDouble(Grade::getGrade).min().orElse(0);
            double maxGrade = grades.stream().mapToDouble(Grade::getGrade).max().orElse(0);

            model.addAttribute("grades", grades);
            model.addAttribute("courseName", courseName);
            model.addAttribute("averageGrade", averageGrade);
            model.addAttribute("minGrade", minGrade);
            model.addAttribute("maxGrade", maxGrade);
        }
        return "showCourseGrades";
    }

    @PostMapping("/studentGrades")
    @Secured("ROLE_teacher")
    public String getStudentGrades(@RequestParam("studentName") String studentName,
                                   Model model) {
        String username = getUsername();

        if (!userService.userExists(studentName)) {
            model.addAttribute("resultMessage", "Student not found.");
            return getTeacherDashboard(model);
        }

        List<Grade> grades = gradeService.getGradesByStudentName(studentName);
        List<Grade> authorizedGrades = filterGradesByTeacherCourses(grades, username);

        if (authorizedGrades.isEmpty()) {
            model.addAttribute("resultMessage", "No grades found for " + studentName);
        } else {
            model.addAttribute("grades", authorizedGrades);
        }
        return "showGrades";
    }

    private List<Grade> filterGradesByTeacherCourses(List<Grade> grades, String username) {
        List<Course> teacherCourses = courseService.getTeacherCoursesByName(username);
        List<Grade> authorizedGrades = new ArrayList<>();

        for (Grade grade : grades) {
            boolean isAuthorized = teacherCourses.stream()
                    .anyMatch(course -> course.getName().equals(grade.getCourseName()));
            if (isAuthorized) {
                authorizedGrades.add(grade);
            }
        }
        return authorizedGrades;
    }

    private boolean isCourseAuthorized(String courseName, String username) {
        List<Course> teacherCourses = courseService.getTeacherCoursesByName(username);
        return teacherCourses.stream().anyMatch(course -> course.getName().equals(courseName));
    }
}
