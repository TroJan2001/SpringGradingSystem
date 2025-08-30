package main.DAO;

import main.Model.Course;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public class CoursesDAO implements CoursesDAOI{

    private final JdbcTemplate jdbcTemplate;

    public CoursesDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public boolean addCourse(Course course) throws DataAccessException {
        if (!isTeacher(course.getTeacherId())) {
            return false;
        }

        if (courseExists(course.getName(), course.getTeacherId())) {
            return false;
        }

        String insertQuery = "INSERT INTO courses (name, teacher_id) VALUES (?, ?)";
        int affectedRows = jdbcTemplate.update(insertQuery, course.getName(), course.getTeacherId());
        return affectedRows > 0;
    }

    public boolean updateCourse(Course course) throws DataAccessException{
        if (!isTeacher(course.getTeacherId())) {
            return false;
        }

        String query = "UPDATE courses SET name = ?, teacher_id = ? WHERE id = ?";
        int affectedRows = jdbcTemplate.update(query, course.getName(), course.getTeacherId(), course.getId());
        return affectedRows > 0;
    }

    public boolean deleteCourse(int id) throws DataAccessException{
        String query = "DELETE FROM courses WHERE id = ?";
        int affectedRows = jdbcTemplate.update(query, id);
        return affectedRows > 0;
    }

    private boolean isTeacher(int teacherId) throws DataAccessException{
        String query = "SELECT COUNT(*) FROM users WHERE id = ? AND role = 'teacher'";
        Integer count = jdbcTemplate.queryForObject(query, Integer.class, teacherId);
        return count != null && count > 0;
    }

    public boolean courseExists(String courseName, int teacherId) throws DataAccessException{
        String checkQuery = "SELECT COUNT(*) FROM courses WHERE name = ? AND teacher_id = ?";
        Integer count = jdbcTemplate.queryForObject(checkQuery, Integer.class, courseName, teacherId);
        return count != null && count > 0;
    }

    public List<Course> getTeacherCoursesByName(String teacherName) throws DataAccessException{
        String query = "SELECT c.id, c.name, c.teacher_id " +
                "FROM courses c " +
                "JOIN users u ON c.teacher_id = u.id " +
                "WHERE u.name = ?";
        return jdbcTemplate.query(query, (rs, rowNum) -> new Course(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getInt("teacher_id")
        ), teacherName);
    }

    public List<Course> getAllCourses() throws DataAccessException{
        String query = "SELECT * FROM courses";
        return jdbcTemplate.query(query, (rs, rowNum) -> new Course(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getInt("teacher_id")
        ));
    }
}
