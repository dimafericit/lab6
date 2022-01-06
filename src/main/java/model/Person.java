package model;

public class Person {
    private String name;
    private String vorname;

    public Person(String name, String vorname){
        this.name = name;
        this.vorname = vorname;
    }

    public String getVorname() {
        return vorname;
    }

    public String getName() {
        return name;
    }

    public void setVorname(String vorname) {
        this.vorname = vorname;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "{"  + name + ' ' +
                vorname +
                '}';
    }
}
