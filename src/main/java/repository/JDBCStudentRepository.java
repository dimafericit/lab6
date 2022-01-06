package repository;

import model.Student;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * using MySQl 8.0 workbench
 *
 * Repository class that saves data to the database
 *
 * university.student table
 * idstudent int primary key not null
 * firstname varchar(45)
 * lastname varchar(45)
 *
 * enrolledcourses: courses taken from university.enrolled table (relation: many to many)
 */
public class JDBCStudentRepository implements ICrudRepository<Student> {

    private String url;
    private String user;
    private String password;


    /**
     * @param url database url
     * @param password database password
     * @param user database user
     */
    public JDBCStudentRepository(String url, String password, String user){
        this.url = url;
        this.password= password;
        this.user = user;
    }

    /**
     * @param stud the student object we want to add to the database
     * @throws SQLException
     */
    @Override
    public void create(Student stud) throws SQLException {
        Connection connection = DriverManager.getConnection(this.url, this.user, this.password);
        Statement statement = connection.createStatement();

        String insertStudent = String.format("INSERT INTO student(idstudent, firstname, lastname) VALUES (%2d, \"%s\", \"%s\")",
                stud.getStudentid(), stud.getVorname(), stud.getName());
        statement.execute(insertStudent);

        statement.close();
        connection.close();
    }

    /**
     * @return the list of students
     * @throws SQLException
     */
    @Override
    public List<Student> getAll() throws SQLException {
        Connection connection = DriverManager.getConnection(this.url, this.user, this.password);
        Statement statement = connection.createStatement();

        //we read the data from the database
        List<Student> students = new ArrayList<>();
        String selectStudents = "SELECT * FROM student";
        ResultSet resultSet = statement.executeQuery(selectStudents);

        /* we add every student to the list
        and search in the enrolled table for the courses the is enrolled to
         */
        while(resultSet.next()){
            Long studentId = resultSet.getLong("idstudent");
            String vorname = resultSet.getString("firstname");
            String name = resultSet.getString("lastname");

            List<Long> courses = new ArrayList<>();
            String selectCoursesEnrolled = String.format("SELECT idcourse FROM enrolled WHERE idstudent=%2d",studentId);
            Statement courseStatement = connection.createStatement();
            ResultSet Courses = courseStatement.executeQuery(selectCoursesEnrolled);


            while (Courses.next()){
                courses.add(Courses.getLong("idcourse"));
            }
            courseStatement.close();

            Statement creditsStatement = connection.createStatement();
            String getCredits = String.format("Select cast(Sum(course.credits) as char(255)) as sum\n" +
                    "From course\n" +
                    "inner join enrolled on course.idcourse = enrolled.idcourse\n" +
                    "Where idstudent = %2d",studentId);
            ResultSet creditsSet = creditsStatement.executeQuery(getCredits);
            int credits = 0;
            if (creditsSet.next()){
                credits = creditsSet.getInt("sum");
            }
            creditsStatement.close();

            students.add(new Student(name, vorname, studentId, courses, credits));
        }

        statement.close();
        connection.close();
        return students;
    }

    /**
     * @param stud the Student object we want to delete out of the database
     * @throws SQLException
     */
    @Override
    public void delete(Student stud) throws SQLException {
        Connection connection = DriverManager.getConnection(this.url, this.user, this.password);

        Statement statement = connection.createStatement();
        String deleteStudent = String.format(" DELETE FROM student WHERE idstudent = %2d",
                stud.getStudentid());
        statement.execute(deleteStudent);

        Statement statement2 = connection.createStatement();
        String deleteStudentfromEnrolled = String.format(" DELETE FROM enrolled WHERE idstudent = %2d",
                stud.getStudentid());
        statement.execute(deleteStudentfromEnrolled);

        statement.close();
        connection.close();
    }

    /**
     * @param stud the student we want to save
     * @throws SQLException
     */
    @Override
    public void update(Student stud) throws SQLException {
        Connection connection = DriverManager.getConnection(this.url, this.user, this.password);
        Statement statement = connection.createStatement();

        //we enable to update a parent/child row
        String enableChanges = "SET FOREIGN_KEY_CHECKS=0";
        Statement statementc = connection.createStatement();
        statementc.execute(enableChanges);

        String updateStudent = String.format("UPDATE student SET firstname=\"%s\", lastName=\"%s\" WHERE idstudent=%2d",
                stud.getVorname(), stud.getName(), stud.getStudentid());
        statement.execute(updateStudent);

        List<Long> updatedCourses = stud.getEnrolledcourse();

        String getEnrollment = String.format("SELECT idcourse FROM enrolled WHERE idstudent=%2d", stud.getStudentid());
        Statement statement1 = connection.createStatement();
        ResultSet Courses = statement1.executeQuery(getEnrollment);

        // if the student has less courses we delete them
        while (Courses.next()){
            long courseId = Courses.getLong("idcourse");
            if (!updatedCourses.contains(courseId)){
                Statement statement2 = connection.createStatement();
                statement2.execute(String.format("DELETE FROM enrolled WHERE studentId=%2d AND courseId=%2d", stud.getStudentid(), courseId));
                statement2.close();
            } else {
                updatedCourses.remove(courseId);
            }
        }

        // if the students has extra courses we add them to the list
        if (!updatedCourses.isEmpty()) {
            Statement statement3 = connection.createStatement();
            statement3.execute(String.format("INSERT INTO enrolled(studentid, courseid) VALUES (%2d, %2d)", stud.getStudentid(), updatedCourses.get(0)));
            statement3.close();
        }

        //we disable to update a parent/child row
        String disableChanges = "SET FOREIGN_KEY_CHECKS=1";
        Statement statementd = connection.createStatement();
        statementc.execute(disableChanges);

        statement.close();
        connection.close();


        }

    /**
     * sorts the list of students ascending after the id
     * @return the sorted list
     * @throws SQLException
     */
        public List<Student> sort() throws SQLException {
            Connection connection = DriverManager.getConnection(this.url, this.user, this.password);
            Statement statement = connection.createStatement();

            List<Student> students = new ArrayList<>();
            String orderby = "SELECT * FROM student ORDER BY idstudent";
            ResultSet resultSet = statement.executeQuery(orderby);

            while(resultSet.next()){
                Long studentId = resultSet.getLong("idstudent");
                String vorname = resultSet.getString("firstname");
                String name = resultSet.getString("lastname");

                List<Long> courses = new ArrayList<>();
                String selectCoursesEnrolled = String.format("SELECT idcourse FROM enrolled WHERE idstudent=%2d",studentId);
                Statement courseStatement = connection.createStatement();
                ResultSet Courses = courseStatement.executeQuery(selectCoursesEnrolled);

                while (Courses.next()){
                    courses.add(Courses.getLong("idcourse"));
                }
                courseStatement.close();

                Statement creditsStatement = connection.createStatement();
                String getCredits = String.format("Select cast(Sum(course.credits) as char(255)) as sum\n" +
                        "From course\n" +
                        "inner join enrolled on course.idcourse = enrolled.idcourse\n" +
                        "Where idstudent = %2d",studentId);
                ResultSet creditsSet = creditsStatement.executeQuery(getCredits);
                int credits = 0;
                if (creditsSet.next()){
                    credits = creditsSet.getInt("sum");
                }
                creditsStatement.close();

                students.add(new Student(name, vorname, studentId, courses, credits));
            }

            statement.close();
            connection.close();
            return students;
        }
}


