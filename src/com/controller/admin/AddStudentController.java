package com.controller.admin;

import com.dao.admin.StudentDAO;
import com.model.Student;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

import java.io.File;

public class AddStudentController {

    @FXML private TextField firstName;
    @FXML private TextField lastName;
    @FXML private TextField regNo;
    @FXML private TextField nic;
    @FXML private DatePicker dob;

    @FXML private RadioButton male;
    @FXML private RadioButton female;
    @FXML private ToggleGroup genderGroup;

    @FXML private ComboBox<String> district;
    @FXML private ImageView studentImageView;

    @FXML private TextField email;
    @FXML private TextField phone;
    @FXML private TextArea address;

    @FXML private ComboBox<String> department;
    @FXML private ComboBox<String> course;
    @FXML private TextField year;
    @FXML private TextField mentor;

    @FXML private TextField guardianName;
    @FXML private TextField guardianPhone;
    @FXML private TextField guardianRelationship;

    private File selectedFile;
    private final StudentDAO studentDAO = new StudentDAO();

    @FXML
    private void uploadImage() {
        FileChooser fc = new FileChooser();
        fc.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg")
        );

        File file = fc.showOpenDialog(studentImageView.getScene().getWindow());

        if (file != null) {
            selectedFile = file;
            studentImageView.setImage(new Image(file.toURI().toString()));
        }
    }

    @FXML
    private void register() {

        String gender = null;
        if (male.isSelected()) gender = "Male";
        else if (female.isSelected()) gender = "Female";

        String imagePath = (selectedFile != null) ? selectedFile.getAbsolutePath() : null;

        Student s = new Student(
                firstName.getText(),
                lastName.getText(),
                regNo.getText(),
                nic.getText(),
                dob.getValue(),
                gender,
                imagePath,
                district.getValue(),

                email.getText(),
                phone.getText(),
                address.getText(),

                department.getValue(),
                course.getValue(),
                year.getText(),
                mentor.getText(),

                guardianName.getText(),
                guardianPhone.getText(),
                guardianRelationship.getText()
        );

        boolean saved = studentDAO.saveStudent(s);

        if (saved) {
            System.out.println("Student saved successfully");
            handleClear();
        } else {
            System.out.println("Error saving student");
        }
    }

    @FXML
    private void handleClear() {
        firstName.clear();
        lastName.clear();
        regNo.clear();
        nic.clear();
        dob.setValue(null);

        genderGroup.selectToggle(null);

        district.setValue(null);
        studentImageView.setImage(null);
        selectedFile = null;

        email.clear();
        phone.clear();
        address.clear();

        department.setValue(null);
        course.setValue(null);
        year.clear();
        mentor.clear();

        guardianName.clear();
        guardianPhone.clear();
        guardianRelationship.clear();
    }
}