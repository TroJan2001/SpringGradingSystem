package main.DAO;

import main.Model.Grade;

import java.util.List;

public interface GradesDAOI {
    boolean addGrade(Grade grade, String username);

    boolean updateGrade(Grade grade, String username);

    boolean deleteGrade(Grade grade, String username);

    List<Grade> getCourseGrades(String courseName);

    List<Grade> getGradesByStudentName(String name);
}
