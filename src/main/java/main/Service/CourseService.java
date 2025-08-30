package main.Service;

import main.DAO.CoursesDAO;
import main.Model.Course;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourseService {

    private final CoursesDAO coursesDAO;

    @Autowired
    public CourseService(CoursesDAO coursesDAO) {
        this.coursesDAO = coursesDAO;
    }

    public boolean addCourse(Course course) {
        try {
            return coursesDAO.addCourse(course);
        }
        catch (DataAccessException e){
            return false;
        }
    }

    public boolean updateCourse(Course course) {
        try {
            return coursesDAO.updateCourse(course);
        }
        catch (DataAccessException e){
            return false;
        }
    }

    public boolean deleteCourse(int id) {
        try {
            return coursesDAO.deleteCourse(id);
        }
        catch (DataAccessException e){
            return false;
        }
    }

    public List<Course> getTeacherCoursesByName(String teacherName) {
        try {
            return coursesDAO.getTeacherCoursesByName(teacherName);
        } catch (DataAccessException e){
            return null;
        }

    }

    public List<Course> getAllCourses() {
        try {
            return coursesDAO.getAllCourses();
        } catch (DataAccessException e){
            return null;
        }
    }

    public boolean courseExists(Course course) {
        try {
            return coursesDAO.courseExists(course.getName(), course.getTeacherId());
        } catch (DataAccessException e){
            return false;
        }

    }
}
