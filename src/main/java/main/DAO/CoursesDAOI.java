package main.DAO;

import main.Model.Course;

import java.util.List;

public interface CoursesDAOI {
    boolean addCourse(Course course);

    boolean updateCourse(Course course);

    boolean deleteCourse(int id);

    List<Course> getAllCourses();

    List<Course> getTeacherCoursesByName(String teacherName);
}
