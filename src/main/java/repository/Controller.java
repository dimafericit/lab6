package repository;

import model.Course;
import model.Student;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Controller {

    private JDBCStudentRepository studenten;
    private JDBCCourseRepository course;


    /**
     * Constructor
     * initializes two repos (for Students and Courses)
     * connects to the Database
     */
    public Controller() {
        studenten = new JDBCStudentRepository("jdbc:mysql://localhost:3306/university",
                "12345678", "root");
        course = new JDBCCourseRepository("jdbc:mysql://localhost:3306/university",
                "12345678", "root");
    }


    /**
     * function to register a student to a course using id's
     * if the id's are valid we are adding the student to the course and incrementing his credits
     *
     * @param cursid id of the course
     * @param studid id of the student we want to register
     * @throws Exception when the student or the course doesn't exist
     * @throws Exception when the course doesn't have enough places or the student doesn't
     *                   have enough credits
     * @throws Exception when she student is already enrolled for the course
     */
    public void register(long cursid, long studid) throws Exception {
        Student studentaux = null;
        Course cursaux = null;

        for (Course elem : this.course.getAll())
            if (elem.getCourseid() == cursid) {
                cursaux = elem;
                break;
            }

        for (Student elem : this.studenten.getAll())
            if (elem.getStudentid() == studid) {
                studentaux = elem;
                break;
            }

        if (cursaux == null || studentaux == null)
            throw new Exception("Student or Course doesn't exist");

        if (studentaux.getEnrolledcourse().contains(cursaux.getCourseid()))
            throw new Exception("Student already enrolled");

        if (cursaux.free() && ((cursaux.getCredits() + studentaux.getCredits()) <= 30)) {
            cursaux.addStudent(studid);
            studentaux.addCourse(cursid);
            studentaux.setCredits(studentaux.getCredits() + cursaux.getCredits());
        } else throw new Exception("Course doesn't have free places or Student " +
                "doesn't have credits left");
        course.update(cursaux);
        studenten.update(studentaux);
    }


    /**
     * function that returns every student enrolled for a course
     *
     * @param courseId id of the course
     * @return a list with the students enrolled for the course given as parameter
     */
    public List<Student> retrieveStudentsEnrolledForACourse(long courseId) throws SQLException {
        List<Student> studentsEnrolledForTheCourse = new LinkedList<>();
        for (Student student : studenten.getAll()) {
            if (student.getEnrolledcourse().contains(courseId)) {
                studentsEnrolledForTheCourse.add(student);
            }
        }
        return studentsEnrolledForTheCourse;
    }


    /**
     * @return al courses with free places
     */
    public List<Course> retrieveCoursesWithFreePlaces() throws SQLException {
        List<Course> freeCourses = new ArrayList<>();
        for (Course index : this.course.getAll())
            if (index.free()) {
                freeCourses.add(index);
            }
        return freeCourses;
    }

    public void sortStudents() throws SQLException {
        System.out.println(studenten.sort());
    }

    public void sortCourses() throws SQLException {
        System.out.println(course.sort());
    }

    public void printStudents() throws SQLException {
        System.out.println(studenten.getAll());
    }

    public void printCourses() throws SQLException {
        System.out.println(course.getAll());
    }

    public List<Student> filterStudents() throws SQLException {
        List<Student> students = studenten.getAll();
        return students.stream().filter(stud -> stud.getCredits() > 0).toList();
    }

    public List<Course> filterCourses() throws SQLException {
        List<Course> courses = course.getAll();
        return courses.stream().filter(curs -> curs.getCredits() == 6).toList();
    }

    public void addStudent(Student student) throws SQLException {
        studenten.create(student);
    }

    public int getCredits(Student student) throws SQLException {
        int nrCredits = 0;
        for (long courseId : student.getEnrolledcourse()) {
            for (Course course : course.getAll()) {
                if (course.getCourseid() == courseId) {
                    nrCredits += course.getCredits();
                }
            }
        }
        return nrCredits;
    }

    public Student returnStudentbyId (Long id) throws SQLException {
        for (Student stud : studenten.getAll()){
            if (stud.getStudentid() == id)
                return stud;
        }
        return null;
    }

    public boolean validateStudent(Long id) throws SQLException {
        for (Student stud : studenten.getAll()){
            if (stud.getStudentid() == id)
                return true;
        }
        return false;
    }
}