package com.controller.admin;

import com.dao.admin.LecturerDAO;
import com.dao.admin.UserDAO;
import com.model.Lecturer;
import com.model.User;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

import java.io.File;

public class AddLecturerController {

    @FXML private TextField firstName;
    @FXML private TextField lastName;
    @FXML private TextField empId;
    @FXML private TextField nic;
    @FXML private DatePicker dob;

    @FXML private RadioButton male;
    @FXML private RadioButton female;
    @FXML private ToggleGroup genderGroup;

    @FXML private ComboBox<String> district;
    @FXML private ImageView lecturerImageView;

    @FXML private TextField email;
    @FXML private TextField phone;
    @FXML private TextArea address;

    @FXML private ComboBox<String> department;
    @FXML private TextField specialization;
    @FXML private TextField designation;
    @FXML private TextField qualification;

    private File selectedFile;

    private final LecturerDAO lecturerDAO = new LecturerDAO();
    private final UserDAO userDAO = new UserDAO();

    @FXML
    private void uploadImage() {
        FileChooser fc = new FileChooser();
        fc.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg")
        );

        File file = fc.showOpenDialog(lecturerImageView.getScene().getWindow());

        if (file != null) {
            selectedFile = file;
            lecturerImageView.setImage(new Image(file.toURI().toString()));
        }
    }

    @FXML
    private void register() {
        String employeeId = empId.getText().trim();

        if (employeeId.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Employee ID is required.");
            return;
        }

        if (firstName.getText().trim().isEmpty() || lastName.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "First name and last name are required.");
            return;
        }

        if (lecturerDAO.existsByEmpId(employeeId)) {
            showAlert(Alert.AlertType.ERROR, "Duplicate Error", "Employee ID already exists.");
            return;
        }

        if (userDAO.existsByUsername(employeeId)) {
            showAlert(Alert.AlertType.ERROR, "Duplicate Error", "Username already exists.");
            return;
        }

        String gender = null;
        if (male.isSelected()) gender = "Male";
        else if (female.isSelected()) gender = "Female";

        String imagePath = (selectedFile != null) ? selectedFile.getAbsolutePath() : null;

        Lecturer lecturer = new Lecturer(
                firstName.getText().trim(),
                lastName.getText().trim(),
                employeeId,
                nic.getText().trim(),
                dob.getValue(),
                gender,
                imagePath,
                district.getValue(),
                email.getText().trim(),
                phone.getText().trim(),
                address.getText().trim(),
                department.getValue(),
                specialization.getText().trim(),
                designation.getText().trim(),
                qualification.getText().trim()
        );

        User user = new User(employeeId, "12345", "Lecturer", imagePath);

        boolean userSaved = userDAO.saveUser(user);
        if (!userSaved) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to create user account.");
            return;
        }

        boolean lecturerSaved = lecturerDAO.saveLecturer(lecturer);

        if (lecturerSaved) {
            showAlert(Alert.AlertType.INFORMATION, "Success",
                    "Lecturer registered successfully.\nUsername: " + employeeId + "\nPassword: 12345");
            handleClear();
        } else {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to save lecturer details.");
        }
    }

    @FXML
    private void handleClear() {
        firstName.clear();
        lastName.clear();
        empId.clear();
        nic.clear();
        dob.setValue(null);
        genderGroup.selectToggle(null);
        district.setValue(null);
        lecturerImageView.setImage(null);
        selectedFile = null;

        email.clear();
        phone.clear();
        address.clear();

        department.setValue(null);
        specialization.clear();
        designation.clear();
        qualification.clear();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
