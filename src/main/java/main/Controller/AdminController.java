package main.Controller;

import main.Model.Course;
import main.Model.Grade;
import main.Model.User;
import main.Service.CourseService;
import main.Service.GradeService;
import main.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final GradeService gradeService;
    private final CourseService courseService;
    private final UserService userService;

    @Autowired
    public AdminController(GradeService gradeService, CourseService courseService, UserService userService) {
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
    @Secured({"ROLE_admin", "ROLE_super_admin"})
    public String getAdminDashboard(Model model) {
        model.addAttribute("username", getUsername());
        model.addAttribute("role", getRole());
        return "adminDashboard";
    }

    @GetMapping("/userManagement")
    @Secured({"ROLE_admin", "ROLE_super_admin"})
    public String getUserManagement(Model model) {
        model.addAttribute("username", getUsername());
        model.addAttribute("role", getRole());
        return "userManagement";
    }

    @GetMapping("/gradesManagement")
    @Secured({"ROLE_admin", "ROLE_super_admin"})
    public String getGradesManagement(Model model) {
        model.addAttribute("username", getUsername());
        model.addAttribute("role", getRole());
        return "gradesManagement";
    }

    @GetMapping("/courseManagement")
    @Secured({"ROLE_admin", "ROLE_super_admin"})
    public String getCourseManagement(Model model) {
        model.addAttribute("username", getUsername());
        model.addAttribute("role", getRole());
        return "courseManagement";
    }

    @PostMapping("/addCourse")
    @Secured({"ROLE_admin", "ROLE_super_admin"})
    public String handleAddCourse(@RequestParam("courseName") String courseName,
                                  @RequestParam("teacherID") int teacherID,
                                  Model model) {
        Course course = new Course(courseName, teacherID);
        boolean result = courseService.addCourse(course);
        model.addAttribute("resultMessage", result ? "Course added successfully." : "Failed to add course.");
        return getCourseManagement(model);
    }

    @PostMapping("/editCourse")
    @Secured({"ROLE_admin", "ROLE_super_admin"})
    public String handleEditCourse(@RequestParam("courseId") int courseId,
                                   @RequestParam("newCourseName") String newCourseName,
                                   @RequestParam("newTeacherId") int newTeacherId,
                                   Model model) {
        Course course = new Course(courseId, newCourseName, newTeacherId);
        boolean result = courseService.updateCourse(course);
        model.addAttribute("resultMessage", result ? "Course updated successfully." : "Failed to update course.");
        return getCourseManagement(model);
    }

    @PostMapping("/deleteCourse")
    @Secured({"ROLE_admin", "ROLE_super_admin"})
    public String handleDeleteCourse(@RequestParam("courseID") int courseID,
                                     Model model) {
        boolean result = courseService.deleteCourse(courseID);
        model.addAttribute("resultMessage", result ? "Course deleted successfully." : "Failed to delete course.");
        return getCourseManagement(model);
    }

    @PostMapping("/registerUser")
    @Secured({"ROLE_admin", "ROLE_super_admin"})
    public String handleRegisterUser(@RequestParam("username") String username,
                                     @RequestParam("password") String password,
                                     @RequestParam("role") String role,
                                     Model model) {
        if ("admin".equals(role) || "super_admin".equals(role)) {
            model.addAttribute("resultMessage", "You are not authorized to register users with 'admin' or 'super_admin' roles.");
            return getUserManagement(model);
        }

        User user = new User(username, password, role);
        boolean result = userService.createUser(user);
        model.addAttribute("resultMessage", result ? "User registered successfully." : "Registration failed. Username may already exist.");
        return getUserManagement(model);
    }

    @PostMapping("/deleteUser")
    @Secured({"ROLE_admin", "ROLE_super_admin"})
    public String handleDeleteUser(@RequestParam("userID") int userID,
                                   Model model) {
        User user = userService.getUserById(userID);
        if (user == null) {
            model.addAttribute("resultMessage", "User not found.");
            return getUserManagement(model);
        }
        String username = user.getName();
        String userRole = userService.getUserRole(username);
        if ("admin".equals(userRole) || "super_admin".equals(userRole)) {
            model.addAttribute("resultMessage", "You are not authorized to delete users with 'admin' or 'super_admin' roles.");
            return getUserManagement(model);
        }

        if ("teacher".equals(userRole)) {
            List<Course> teacherCourses = courseService.getTeacherCoursesByName(username);
            for (Course course : teacherCourses) {
                courseService.deleteCourse(course.getId());
            }
        }

        boolean result = userService.deleteUser(userID);
        model.addAttribute("resultMessage", result ? "User deleted successfully." : "Failed to delete user.");
        return getUserManagement(model);
    }

    @GetMapping("/courses")
    @Secured({"ROLE_admin", "ROLE_super_admin"})
    public String handleGetAllCourses(Model model) {
        List<Course> courses = courseService.getAllCourses();
        model.addAttribute("courses", courses);
        return "showAllCourses";
    }

    @PostMapping("/studentGrades")
    @Secured({"ROLE_admin", "ROLE_super_admin"})
    public String handleGetGrades(@RequestParam("studentName") String studentName,
                                  Model model) {
        if (!userService.userExists(studentName)) {
            model.addAttribute("resultMessage", "Student not found.");
            return getGradesManagement(model);
        }

        List<Grade> grades = gradeService.getGradesByStudentName(studentName);
        if (grades.isEmpty()) {
            model.addAttribute("resultMessage", "No grades found for " + studentName);
            return getGradesManagement(model);
        } else {
            model.addAttribute("grades", grades);
            return "showGrades";
        }
    }

    @PostMapping("/courseGrades")
    @Secured({"ROLE_admin", "ROLE_super_admin"})
    public String handleGetCourseGrades(@RequestParam("courseName") String courseName,
                                        @RequestParam("teacherID") int teacherID,
                                        Model model) {
        Course course = new Course(courseName, teacherID);
        if (!courseService.courseExists(course)) {
            model.addAttribute("resultMessage", "Course not found.");
            return getGradesManagement(model);
        }
        List<Grade> grades = gradeService.getCourseGrades(courseName);
        model.addAttribute("grades", grades);
        model.addAttribute("courseName", courseName);
        return "showCourseGrades";
    }

    @PostMapping("/registerAdmin")
    @Secured("ROLE_super_admin")
    public String handleRegisterAdmin(@RequestParam("adminUsername") String username,
                                      @RequestParam("adminPassword") String password,
                                      Model model) {
        if (username == null || password == null) {
            model.addAttribute("resultMessage", "Username or password cannot be empty.");
            return getUserManagement(model);
        }

        User user = new User(username, password, "admin");
        boolean result = userService.createUser(user);
        model.addAttribute("resultMessage", result ? "Admin registered successfully." : "Failed to register admin.");
        return getUserManagement(model);
    }

    @PostMapping("/user")
    @Secured("ROLE_super_admin")
    public String handleShowUserById(@RequestParam("userID") int userID,
                                     Model model) {

        User user = userService.getUserById(userID);
        if (user == null) {
            model.addAttribute("resultMessage", "User not found.");
            return getUserManagement(model);
        }
        String username = user.getName();
        if (userService.userExists(username)) {
            model.addAttribute("user", user);
            return "showUser";
        } else {
            model.addAttribute("resultMessage", "User not found");
            return getUserManagement(model);
        }
    }

    @PostMapping("/users")
    @Secured("ROLE_super_admin")
    public String handleShowAllUsers(Model model) {
        List<User> users = userService.getAllUsers();
        if (!users.isEmpty()) {
            model.addAttribute("users", users);
            return "showAllUsers";
        } else {
            model.addAttribute("resultMessage", "No users found.");
            return getUserManagement(model);
        }
    }

}
