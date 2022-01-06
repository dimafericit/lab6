package model;

import java.util.ArrayList;
import java.util.List;

public class Teacher extends Person {
    private List<Course> course;
    private long idteacher;

    public Teacher(String name, String vorname, long id){
        super(name, vorname);
        this.idteacher = id;
    }

    public long getIdteacher() {
        return idteacher;
    }
}
