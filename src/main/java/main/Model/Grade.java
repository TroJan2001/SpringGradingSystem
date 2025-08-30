package main.Model;

public class Grade {
    private final String studentName;
    private final String courseName;
    private final float grade;

    public Grade(String studentName, String courseName, float grade) {
        this.studentName = studentName;
        this.courseName = courseName;
        this.grade = grade;
    }

    public String getCourseName() {
        return courseName;
    }

    public String getStudentName() {
        return studentName;
    }

    public float getGrade() {
        return grade;
    }

    @Override
    public String toString() {
        return "Entity.Grade{" +
                ", studentName=" + studentName +
                ", courseName=" + courseName +
                ", grade=" + grade +
                '}';
    }
}
