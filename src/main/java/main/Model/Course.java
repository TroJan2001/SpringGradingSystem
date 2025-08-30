package main.Model;

public class Course {
    private int id;
    private final String name;
    private final int teacherId;

    public Course(int id, String name, int teacherId) {
        this.id = id;
        this.name = name;
        this.teacherId = teacherId;
    }

    public Course(String name, int teacherId) {
        this.name = name;
        this.teacherId = teacherId;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getTeacherId() {
        return teacherId;
    }


    @Override
    public String toString() {
        return "Entity.Course{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", teacherId=" + teacherId +
                '}';
    }
}
