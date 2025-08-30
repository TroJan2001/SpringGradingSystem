package main.DAO;

import main.Model.Grade;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class GradesDAO implements GradesDAOI{

    private final JdbcTemplate jdbcTemplate;

    public GradesDAO(JdbcTemplate jdbcTemplate) throws DataAccessException {
        this.jdbcTemplate = jdbcTemplate;
    }

    public boolean addGrade(Grade grade, String username) {
        if (!validateInputs(grade.getCourseName(), grade.getStudentName(), username)) {
            return false;
        }

        if (gradeExists(grade.getStudentName(), grade.getCourseName())) {
            return false;
        }

        String insertQuery = "INSERT INTO grades (student_name, course_name, grade) VALUES (?, ?, ?)";
        return executeUpdate(insertQuery, grade.getStudentName(), grade.getCourseName(), grade.getGrade());
    }

    public boolean updateGrade(Grade grade, String username) throws DataAccessException{
        if (!validateInputs(grade.getCourseName(), grade.getStudentName(), username)) {
            return false;
        }

        String updateQuery = "UPDATE grades SET grade = ? WHERE student_name = ? AND course_name = ?";
        return executeUpdate(updateQuery, grade.getGrade(), grade.getStudentName(), grade.getCourseName());
    }

    public boolean deleteGrade(Grade grade, String username) throws DataAccessException{
        if (!validateInputs(grade.getCourseName(), grade.getStudentName(), username)) {
            return false;
        }

        String deleteQuery = "DELETE FROM grades WHERE student_name = ? AND course_name = ?";
        return executeUpdate(deleteQuery, grade.getStudentName(), grade.getCourseName());
    }

    public List<Grade> getCourseGrades(String courseName) throws DataAccessException{
        String query = "SELECT student_name, grade FROM grades WHERE course_name = ?";
        return jdbcTemplate.query(query, (rs, rowNum) -> new Grade(
                rs.getString("student_name"),
                courseName,
                rs.getFloat("grade")
        ), courseName);
    }

    public List<Grade> getGradesByStudentName(String studentName) throws DataAccessException{
        String query = "SELECT course_name, grade FROM grades WHERE student_name = ?";
        return jdbcTemplate.query(query, (rs, rowNum) -> new Grade(
                studentName,
                rs.getString("course_name"),
                rs.getFloat("grade")
        ), studentName);
    }

    private boolean validateInputs(String courseName, String studentName, String username) {
        return courseExists(courseName) && studentExists(studentName) && isAuthorized(username, courseName);
    }

    private boolean courseExists(String courseName) throws DataAccessException{
        return exists("SELECT COUNT(*) FROM courses WHERE name = ?", courseName);
    }

    private boolean studentExists(String studentName) throws DataAccessException{
        return exists("SELECT COUNT(*) FROM users WHERE name = ? AND role = 'student'", studentName);
    }

    private boolean exists(String query, String param) throws DataAccessException{
        Integer count = jdbcTemplate.queryForObject(query, Integer.class, param);
        return count != null && count > 0;
    }

    private boolean gradeExists(String studentName, String courseName) throws DataAccessException{
        String query = "SELECT COUNT(*) FROM grades WHERE student_name = ? AND course_name = ?";
        Integer count = jdbcTemplate.queryForObject(query, Integer.class, studentName, courseName);
        return count != null && count > 0;
    }

    private boolean executeUpdate(String query, Object... params) throws DataAccessException{
        int affectedRows = jdbcTemplate.update(query, params);
        return affectedRows > 0;
    }

    private boolean isAuthorized(String username, String courseName) throws DataAccessException{
        String query = "SELECT u.role, c.teacher_id " +
                "FROM users u " +
                "JOIN courses c ON c.name = ? " +
                "WHERE u.name = ?";

        return Boolean.TRUE.equals(jdbcTemplate.query(query, rs -> {
            if (rs.next()) {
                String role = rs.getString("role");
                int teacherId = rs.getInt("teacher_id");

                if ("admin".equals(role) || "super_admin".equals(role)) {
                    return true;
                }

                return checkTeacherAuthorization(username, teacherId);
            }
            return false;
        }, courseName, username));
    }

    private boolean checkTeacherAuthorization(String username, int teacherId) throws DataAccessException{
        String teacherQuery = "SELECT COUNT(*) FROM users WHERE name = ? AND id = ?";
        Integer count = jdbcTemplate.queryForObject(teacherQuery, Integer.class, username, teacherId);
        return count != null && count > 0;
    }
}
