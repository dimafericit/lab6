package model;

import java.util.ArrayList;
import java.util.List;

public class Course {
    private static int next_id = 10000;
    private long courseid;
    private String name;
    private Long teacher;
    private int maxenrolled;
    private List<Long> enrolledstudents;
    private int credits;

    public Course(String name, Long teacher, int max, int credits){
        this.courseid = ++next_id;
        this.name = name;
        this.teacher = teacher;
        this.maxenrolled = max;
        this.credits = credits;
        this.enrolledstudents = new ArrayList<>();
    }

    public Course(String name, Long teacher, int max, int credits, long id){
        this.courseid = id;
        this.name = name;
        this.teacher = teacher;
        this.maxenrolled = max;
        this.credits = credits;
        this.enrolledstudents = new ArrayList<>();
    }

    public Course(String name, Long teacher, int max, int credits, long id, List<Long> list){
        this.courseid = id;
        this.name = name;
        this.teacher = teacher;
        this.maxenrolled = max;
        this.credits = credits;
        this.enrolledstudents = list;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCredits() {
        return credits;
    }

    public void setCredits(int credits) {
        this.credits = credits;
    }

    public int getMaxenrolled() {
        return maxenrolled;
    }

    public void setMaxenrolled(int maxenrolled) {
        this.maxenrolled = maxenrolled;
    }

    public Long getTeacher() {
        return teacher;
    }

    public List<Long> getEnrolledstudents() {
        return enrolledstudents;
    }

    public void addStudent(Long stud) {
        this.enrolledstudents.add(stud);
    }

    public boolean free(){
        return this.maxenrolled > this.enrolledstudents.size();
    }

    public void print(){
        for (long index: enrolledstudents)
            System.out.println(index);
    }

    @Override
    public String toString() {
        return "Course{" +
                "id='" + courseid + '\'' +
                "name='" + name + '\'' +
                ", teacher=" + teacher +
                ", maxenrolled=" + maxenrolled +
                ", credits=" + credits +
                '}';
    }

    public long getCourseid() {
        return courseid;
    }

    public int compareTo(Course obj){
        return this.credits - obj.getCredits();
    }
}
