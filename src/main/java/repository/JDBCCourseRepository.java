package repository;

import model.Course;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * using MySQl 8.0 workbench
 *
 * Repository class that saves data to the database
 *
 * university.course table
 * idcourse int primary key not null
 * name varchar(45)
 * credits int
 * teacher int foreign key
 * maxenrolled int
 *
 */
public class JDBCCourseRepository implements ICrudRepository<Course> {
    private final String url;
    private final String user;
    private final String password;


    /**
     * @param url database url
     * @param password database password
     * @param user database user
     */
    public JDBCCourseRepository(String url, String password, String user){
        this.url = url;
        this.password= password;
        this.user = user;
    }

    /**
     * adds a course to the database
     * @param curs the Course object we want to add
     * @throws SQLException
     */
    @Override
    public void create(Course curs) throws SQLException {
        Connection connection = DriverManager.getConnection(this.url, this.user, this.password);
        Statement statement = connection.createStatement();

        String insertCourse = String.format("INSERT INTO course(idcourse, name, teacher, maxenrolled, credits) " +
                        "VALUES (%2d, \"%s\", %2d, %2d, %2d)",
                curs.getCourseid(), curs.getName(), curs.getTeacher(), curs.getMaxenrolled(), curs.getCredits());
        statement.execute(insertCourse);

        statement.close();
        connection.close();
    }

    /**
     * @return a list of courses from the database
     * @throws SQLException
     */
    @Override
    public List<Course> getAll() throws SQLException {
        Connection connection = DriverManager.getConnection(this.url, this.user, this.password);
        Statement statement = connection.createStatement();

        List<Course> courses = new ArrayList<>();

        String selectCourses = "SELECT * FROM course";
        ResultSet resultSet = statement.executeQuery(selectCourses);
        while (resultSet.next()){
            long courseId = resultSet.getLong("idcourse");
            String name = resultSet.getString("name");
            long teacher = resultSet.getLong("teacher");
            int maxenrolled = resultSet.getInt("maxenrolled");
            int credits = resultSet.getInt("credits");


            List<Long> students = new ArrayList<>();
            String selectStudentsEnrolled = String.format("SELECT idstudent FROM enrolled WHERE idcourse=%2d",courseId);
            Statement statement1 = connection.createStatement();
            ResultSet enrolledStudents = statement1.executeQuery(selectStudentsEnrolled);
            while (enrolledStudents.next()){
                students.add(enrolledStudents.getLong("idstudent"));
            }
            statement1.close();

            courses.add(new Course(name, teacher, maxenrolled, credits, courseId, students));
        }

        statement.close();
        connection.close();
        return courses;
    }

    /**
     * deletes a course from the database (from university.enrolled/student)
     * @param curs Course object we want to delete
     * @throws SQLException
     */
    @Override
    public void delete(Course curs) throws SQLException {
        Connection connection = DriverManager.getConnection(this.url, this.user, this.password);
        Statement statement = connection.createStatement();

        String deleteCourse = String.format("DELETE FROM course WHERE idcourse = %2d", curs.getCourseid());
        statement.execute(deleteCourse);

        Statement statement2 = connection.createStatement();
        String deleteCoursefromEnrolled = String.format("DELETE FROM enrolled WHERE idcourse = %2d", curs.getCourseid());
        statement2.execute(deleteCoursefromEnrolled);

        statement.close();
        statement2.close();
        connection.close();
    }

    /**
     * @param curs the course we want to save
     * @throws SQLException
     */
    @Override
    public void update(Course curs) throws SQLException {
        Connection connection = DriverManager.getConnection(this.url, this.user, this.password);
        Statement statement = connection.createStatement();

        //we enable to update a parent/child row
        String enableChanges = "SET FOREIGN_KEY_CHECKS=0";
        Statement statementc = connection.createStatement();
        statementc.execute(enableChanges);

        //updates the course attributes if they are changed
        String updateCourse = String.format("UPDATE course SET  name = \"%s\", " +
                        "maxenrolled = %2d, teacher = %2d, credits = %2d WHERE idcourse=%2d",
                curs.getName(), curs.getMaxenrolled(), curs.getMaxenrolled(), curs.getCredits(), curs.getCourseid());
        statement.execute(updateCourse);

        //updated list of students
        List<Long> updatedStudents = curs.getEnrolledstudents();

        //outdated list of students
        String getStudents = String.format("SELECT idstudent FROM enrolled WHERE idcourse=%2d", curs.getCourseid());
        Statement statement1 = connection.createStatement();
        ResultSet outdatedStudents = statement1.executeQuery(getStudents);

        //if the the student list needs to be updated we delete/add the needed students
        if (!Objects.equals(updatedStudents, outdatedStudents)){
           while (outdatedStudents.next()){
                long studentId = outdatedStudents.getLong("idstudent");
                if (!updatedStudents.contains(studentId)){
                    Statement statement2 = connection.createStatement();
                    statement2.execute(String.format("DELETE FROM enrolled WHERE idstudent=%2d AND idcourse=%2d", studentId, curs.getCourseid()));
                    statement2.close();
                } else {
                    updatedStudents.remove(studentId);
                }
            }
            if (!updatedStudents.isEmpty()) {
                Statement statement3 = connection.createStatement();
                statement3.execute(String.format("INSERT INTO enrolled(idstudent, idcourse) VALUES (%2d, %2d)", updatedStudents.get(0), curs.getCourseid()));
                statement3.close();
            }
        }

        //we disable the option to update a parent/child row
        String disableChanges = "SET FOREIGN_KEY_CHECKS=1";
        Statement statementd = connection.createStatement();
        statementc.execute(disableChanges);

        statement.close();
        connection.close();

    }

    /**
     * sorts the list of courses ascending after maxenroled
     * @return the sorted list
     */
    public List<Course> sort() throws SQLException {
        Connection connection = DriverManager.getConnection(this.url, this.user, this.password);
        Statement statement = connection.createStatement();

        List<Course> courses = new ArrayList<>();

        String selectCourses = "SELECT * FROM course ORDER BY maxenrolled";
        ResultSet resultSet = statement.executeQuery(selectCourses);
        while (resultSet.next()){
            long courseId = resultSet.getLong("idcourse");
            String name = resultSet.getString("name");
            long teacher = resultSet.getLong("teacher");
            int maxenrolled = resultSet.getInt("maxenrolled");
            int credits = resultSet.getInt("credits");


            List<Long> students = new ArrayList<>();
            String selectStudentsEnrolled = String.format("SELECT idstudent FROM enrolled WHERE idcourse=%2d",courseId);
            Statement statement1 = connection.createStatement();
            ResultSet enrolledStudents = statement1.executeQuery(selectStudentsEnrolled);
            while (enrolledStudents.next()){
                students.add(enrolledStudents.getLong("idstudent"));
            }
            statement1.close();

            courses.add(new Course(name, teacher, maxenrolled, credits, courseId, students));
        }

        statement.close();
        connection.close();
        return courses;
    }
}
