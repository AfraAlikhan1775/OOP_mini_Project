package com.controller.Lecturer;

import com.model.Student;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.File;

public class ViewStudentController {

    @FXML private ImageView profileImage;

    @FXML private Label firstNameLabel;
    @FXML private Label lastNameLabel;
    @FXML private Label regNoLabel;
    @FXML private Label nicLabel;
    @FXML private Label dobLabel;
    @FXML private Label genderLabel;
    @FXML private Label districtLabel;
    @FXML private Label emailLabel;
    @FXML private Label phoneLabel;
    @FXML private Label addressLabel;
    @FXML private Label departmentLabel;
    @FXML private Label degreeLabel;
    @FXML private Label yearLabel;

    public void setStudent(Student student) {
        firstNameLabel.setText(value(student.getFirstName()));
        lastNameLabel.setText(value(student.getLastName()));
        regNoLabel.setText(value(student.getRegNo()));
        nicLabel.setText(value(student.getNic()));
        dobLabel.setText(student.getDob() == null ? "" : student.getDob().toString());
        genderLabel.setText(value(student.getGender()));
        districtLabel.setText(value(student.getDistrict()));
        emailLabel.setText(value(student.getEmail()));
        phoneLabel.setText(value(student.getPhone()));
        addressLabel.setText(value(student.getAddress()));
        departmentLabel.setText(value(student.getDepartment()));
        degreeLabel.setText(value(student.getDegrea()));
        yearLabel.setText(value(student.getYear()));

        if (student.getImagePath() != null && !student.getImagePath().isBlank()) {
            File file = new File(student.getImagePath());
            if (file.exists()) {
                profileImage.setImage(new Image(file.toURI().toString()));
            }
        }
    }

    @FXML
    private void handleClose() {
        Stage stage = (Stage) firstNameLabel.getScene().getWindow();
        stage.close();
    }

    private String value(String text) {
        return text == null ? "" : text;
    }
}