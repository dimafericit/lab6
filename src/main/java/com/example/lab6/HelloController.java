package com.example.lab6;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import model.Student;
import repository.Controller;
import repository.JDBCStudentRepository;
import repository.JDBCTeacherRepository;


import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import static java.lang.String.valueOf;

public class HelloController {
    private Stage stage;
    private Scene scene;
    private Parent root;

    private static Long id;

    @FXML
    private Button teacherButton;

    @FXML
    private TextField username;

    @FXML
    private TextField idfield;

    @FXML
    private Button studentButton;

    @FXML
    private TextField teacherTextbox;

    @FXML
    private Label credits;

    @FXML
    private Label studentsLabel;

    @FXML
    private Label error;

    @FXML
    private Label errorRegister;

    @FXML
    private Button regButton;

    @FXML
    private TextField regText;

    public void teacherLogin(ActionEvent event) throws IOException, SQLException {

        this.id = Long.parseLong(idfield.getText());
        JDBCTeacherRepository prof = new JDBCTeacherRepository("jdbc:mysql://localhost:3306/university",
                "12345678", "root");
        if (prof.validateTeacher(this.id)) {
            root = FXMLLoader.load(getClass().getResource("Lehrer.fxml"));
            stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        }
        else error.setText("The Teacher doesn't exist in the database");
    }

    public void showCourses() throws SQLException {
        Controller reg = new Controller();
        List<Student> list = reg.retrieveStudentsEnrolledForACourse(Long.parseLong(teacherTextbox.getText()));

        String text = " ";
        for (Student stud : list){
            text = text + valueOf(stud.getStudentid()) + ":" + stud.getName() + " "+ stud.getVorname() + " ";
        }
        studentsLabel.setText(text);
    }

    public void studentLogin(ActionEvent event) throws IOException, SQLException {

        Controller reg = new Controller();
        if (reg.validateStudent(Long.parseLong(idfield.getText()))) {
            this.id = Long.parseLong(idfield.getText());

            root = FXMLLoader.load(getClass().getResource("Student.fxml"));
            stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        }
        else error.setText("The student doesn't exist in the database");
    }

    @FXML
    public void showCredits() throws SQLException {
        Controller reg = new Controller();
        int nrcred = reg.getCredits(reg.returnStudentbyId(this.id));
        credits.setText(valueOf(nrcred));
    }

    public void registerToCourse(){
        Controller reg = new Controller();
        try {
            reg.register(Long.parseLong(regText.getText()), this.id);
        } catch (Exception e) {
            e.printStackTrace();
            errorRegister.setText("Error, Student can't be added");
        }
    }

    public void backToMain(ActionEvent event) throws IOException {
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        stage.close();
    }

}