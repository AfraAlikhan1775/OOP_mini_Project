package com.controller.admin;

import com.dao.admin.StudentDAO;
import com.dao.admin.UserDAO;
import com.model.Student;
import com.model.User;
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
    private final UserDAO userDAO = new UserDAO();

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

        String registrationNumber = regNo.getText().trim();

        if (registrationNumber.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Registration number is required.");
            return;
        }

        if (firstName.getText().trim().isEmpty() || lastName.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "First name and last name are required.");
            return;
        }

        if (studentDAO.existsByRegNo(registrationNumber)) {
            showAlert(Alert.AlertType.ERROR, "Duplicate Error", "Registration number already exists.");
            return;
        }

        if (userDAO.existsByUsername(registrationNumber)) {
            showAlert(Alert.AlertType.ERROR, "Duplicate Error", "Username already exists.");
            return;
        }

        String gender = null;
        if (male.isSelected()) {
            gender = "Male";
        } else if (female.isSelected()) {
            gender = "Female";
        }

        String imagePath = (selectedFile != null) ? selectedFile.getAbsolutePath() : null;

        Student s = new Student(
                firstName.getText().trim(),
                lastName.getText().trim(),
                registrationNumber,
                nic.getText().trim(),
                dob.getValue(),
                gender,
                imagePath,
                district.getValue(),
                email.getText().trim(),
                phone.getText().trim(),
                address.getText().trim(),
                department.getValue(),
                course.getValue(),
                year.getText().trim(),
                mentor.getText().trim(),
                guardianName.getText().trim(),
                guardianPhone.getText().trim(),
                guardianRelationship.getText().trim()
        );

        User u = new User(
                registrationNumber,
                "12345",
                "Student",
                imagePath
        );

        boolean userSaved = userDAO.saveUser(u);

        if (!userSaved) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to create user account.");
            return;
        }

        boolean studentSaved = studentDAO.saveStudent(s);

        if (studentSaved) {
            showAlert(Alert.AlertType.INFORMATION, "Success",
                    "Student registered successfully.\nUsername: " + registrationNumber + "\nPassword: 12345");
            handleClear();
        } else {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to save student details.");
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

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}