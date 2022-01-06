package repository;

import model.Teacher;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JDBCTeacherRepository implements ICrudRepository<Teacher> {

    private String url;
    private String user;
    private String password;


    /**
     * @param url database url
     * @param password database password
     * @param user database user
     */
    public JDBCTeacherRepository(String url, String password, String user){
        this.url = url;
        this.password= password;
        this.user = user;
    }

    @Override
    public void create(Teacher obj) throws SQLException {

    }

    /**
     * @return the list of students
     * @throws SQLException
     */
    @Override
    public List<Teacher> getAll() throws SQLException {
        Connection connection = DriverManager.getConnection(this.url, this.user, this.password);
        Statement statement = connection.createStatement();

        //we read the data from the database
        List<Teacher> teachers = new ArrayList<>();
        String selectStudents = "SELECT * FROM teacher";
        ResultSet resultSet = statement.executeQuery(selectStudents);

        /* we add every student to the list
        and search in the enrolled table for the courses the is enrolled to
         */
        while(resultSet.next()){
            Long teacherId = resultSet.getLong("idteacher");
            String vorname = resultSet.getString("firstname");
            String name = resultSet.getString("lastname");


            teachers.add(new Teacher(name, vorname, teacherId));
        }

        statement.close();
        connection.close();
        return teachers;
    }

    @Override
    public void update(Teacher obj) throws SQLException {

    }

    @Override
    public void delete(Teacher obj) throws SQLException {

    }

    public boolean validateTeacher(Long id) throws SQLException {
        for (Teacher prof : this.getAll()){
            if (id == prof.getIdteacher())
                return true;
        }
        return false;
    }

}
