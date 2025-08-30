package main.Controller;

import main.Model.Course;
import main.Model.Grade;
import main.Service.CourseService;
import main.Service.GradeService;
import main.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/common")
public class CommonController {

    private final CourseService courseService;
    private final GradeService gradeService;
    private final UserService userService;

    @Autowired
    public CommonController(GradeService gradeService, CourseService courseService, UserService userService) {
        this.courseService = courseService;
        this.gradeService = gradeService;
        this.userService = userService;
    }

    @PostMapping("/teacherCourses")
    @Secured({"ROLE_student", "ROLE_teacher", "ROLE_admin", "ROLE_super_admin"})
    public String showTeacherCourses(@RequestParam("teacherName") String teacherName,
                                     Model model) {

        if (isEmpty(teacherName) || !userService.userExists(teacherName)) {
            model.addAttribute("error", "Invalid teacher name.");
            return "error";
        }

        List<Course> courses = courseService.getTeacherCoursesByName(teacherName);
        model.addAttribute("courses", courses);
        return "showTeacherCourses";
    }

    @PostMapping("/enterGrade")
    @Secured({"ROLE_teacher", "ROLE_admin", "ROLE_super_admin"})
    public String enterGrade(@RequestParam("studentName") String studentName,
                             @RequestParam("courseName") String courseName,
                             @RequestParam("grade") Float grade,
                             Authentication authentication,
                             Model model) {
        String username = authentication.getName();

        if (isEmpty(studentName) || isEmpty(courseName) || grade == null) {
            model.addAttribute("resultMessage", "Failed to enter grade: missing information.");
            return "teacherDashboard";
        }

        Grade newGrade = new Grade(studentName, courseName, grade);
        boolean result = gradeService.addGrade(newGrade, username);
        model.addAttribute("resultMessage", result ? "Grade entered successfully." : "Failed to enter grade.");
        return "teacherDashboard";
    }

    @PostMapping("/editGrade")
    @Secured({"ROLE_teacher", "ROLE_admin", "ROLE_super_admin"})
    public String editGrade(@RequestParam("studentName") String studentName,
                            @RequestParam("courseName") String courseName,
                            @RequestParam("grade") Float grade,
                            Authentication authentication,
                            Model model) {
        String username = authentication.getName();

        if (isEmpty(studentName) || isEmpty(courseName) || grade == null) {
            model.addAttribute("resultMessage", "Failed to edit grade: missing information.");
            return "teacherDashboard";
        }

        Grade updatedGrade = new Grade(studentName, courseName, grade);
        boolean result = gradeService.updateGrade(updatedGrade, username);
        model.addAttribute("resultMessage", result ? "Grade updated successfully." : "Failed to update grade.");
        return "teacherDashboard";
    }

    @PostMapping("/deleteGrade")
    @Secured({"ROLE_teacher", "ROLE_admin", "ROLE_super_admin"})
    public String deleteGrade(@RequestParam("studentName") String studentName,
                              @RequestParam("courseName") String courseName,
                              Authentication authentication,
                              Model model) {
        String username = authentication.getName();

        if (isEmpty(studentName) || isEmpty(courseName)) {
            model.addAttribute("resultMessage", "Failed to delete grade: missing information.");
            return "teacherDashboard";
        }

        Grade gradeToDelete = new Grade(studentName, courseName, 0);
        boolean result = gradeService.deleteGrade(gradeToDelete, username);
        model.addAttribute("resultMessage", result ? "Grade deleted successfully." : "Failed to delete grade.");
        return "teacherDashboard";
    }

    private boolean isEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }
}
