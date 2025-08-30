package main.Service;

import main.DAO.GradesDAO;
import main.Model.Grade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GradeService {

    private final GradesDAO gradesDAO;

    @Autowired
    public GradeService(GradesDAO gradesDAO) {
        this.gradesDAO = gradesDAO;
    }

    public boolean addGrade(Grade grade, String username) {
        try {
            return gradesDAO.addGrade(grade, username);
        } catch (DataAccessException e){
            return false;
        }

    }

    public boolean updateGrade(Grade grade, String username) {
        try {
            return gradesDAO.updateGrade(grade, username);
        } catch (DataAccessException e) {
            return false;
        }
    }

    public boolean deleteGrade(Grade grade, String username) {
        try {
            return gradesDAO.deleteGrade(grade, username);
        } catch (DataAccessException e) {
            return false;
        }

    }

    public List<Grade> getCourseGrades(String courseName) {
        try {
            return gradesDAO.getCourseGrades(courseName);
        } catch (DataAccessException e) {
            return null;
        }
    }

    public List<Grade> getGradesByStudentName(String studentName) {
        try {
            return gradesDAO.getGradesByStudentName(studentName);
        } catch (DataAccessException e) {
            return null;
        }
    }
}
