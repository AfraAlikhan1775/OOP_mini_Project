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
    @FXML private TextField nic;
    @FXML private DatePicker dob;
    @FXML private ComboBox<String> gender;
    @FXML private ImageView lecturerImageView;

    @FXML private TextField contactNumber;
    @FXML private TextField email;
    @FXML private TextField emergencyContact;
    @FXML private ComboBox<String> district;
    @FXML private TextArea address;

    @FXML private TextField employeeId;
    @FXML private ComboBox<String> department;
    @FXML private ComboBox<String> lecturerType;
    @FXML private DatePicker appointmentDate;
    @FXML private TextField specialization;
    @FXML private TextField experienceYears;
    @FXML private TextArea degrees;

    @FXML private ComboBox<String> status;
    @FXML private TextField linkedUsername;

    private File selectedFile;

    private final LecturerDAO lecturerDAO = new LecturerDAO();
    private final UserDAO userDAO = new UserDAO();

    @FXML
    public void initialize() {
        employeeId.textProperty().addListener((obs, oldValue, newValue) -> {
            linkedUsername.setText(newValue.trim());
        });
    }

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
        String empIdValue = employeeId.getText().trim();

        if (empIdValue.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Employee ID is required.");
            return;
        }

        if (firstName.getText().trim().isEmpty() || lastName.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "First name and last name are required.");
            return;
        }

        if (department.getValue() == null) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Department is required.");
            return;
        }

        if (lecturerType.getValue() == null) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Lecturer type is required.");
            return;
        }

        if (lecturerDAO.existsByEmpId(empIdValue)) {
            showAlert(Alert.AlertType.ERROR, "Duplicate Error", "Employee ID already exists.");
            return;
        }

        if (userDAO.existsByUsername(empIdValue)) {
            showAlert(Alert.AlertType.ERROR, "Duplicate Error", "Username already exists.");
            return;
        }

        int experience = 0;
        if (!experienceYears.getText().trim().isEmpty()) {
            try {
                experience = Integer.parseInt(experienceYears.getText().trim());
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR, "Validation Error", "Experience must be a number.");
                return;
            }
        }

        String regPicPath = selectedFile != null ? selectedFile.getAbsolutePath() : null;

        Lecturer lecturer = new Lecturer();
        lecturer.setFirstName(firstName.getText().trim());
        lecturer.setLastName(lastName.getText().trim());
        lecturer.setEmployeeId(empIdValue);
        lecturer.setNic(nic.getText().trim());
        lecturer.setDob(dob.getValue());
        lecturer.setGender(gender.getValue());
        lecturer.setRegPic(regPicPath);
        lecturer.setContactNumber(contactNumber.getText().trim());
        lecturer.setEmail(email.getText().trim());
        lecturer.setEmergencyContact(emergencyContact.getText().trim());
        lecturer.setDistrict(district.getValue());
        lecturer.setAddress(address.getText().trim());
        lecturer.setDepartment(department.getValue());
        lecturer.setLecturerType(lecturerType.getValue());
        lecturer.setAppointmentDate(appointmentDate.getValue());
        lecturer.setSpecialization(specialization.getText().trim());
        lecturer.setExperienceYears(experience);
        lecturer.setStatus(status.getValue());

        User user = new User(empIdValue, "12345", "Lecturer", regPicPath);

        boolean userSaved = userDAO.saveUser(user);
        if (!userSaved) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to create user account.");
            return;
        }

        boolean lecturerSaved = lecturerDAO.saveLecturer(lecturer);
        if (!lecturerSaved) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to save lecturer details.");
            return;
        }

        String degreeText = degrees.getText().trim();
        if (!degreeText.isEmpty()) {
            String[] degreeList = degreeText.split("\\r?\\n");
            for (String degree : degreeList) {
                String cleanedDegree = degree.trim();
                if (!cleanedDegree.isEmpty()) {
                    lecturerDAO.saveLecturerDegree(empIdValue, cleanedDegree);
                }
            }
        }

        showAlert(
                Alert.AlertType.INFORMATION,
                "Success",
                "Lecturer registered successfully.\nUsername: " + empIdValue + "\nPassword: 12345"
        );

        handleClear();
    }

    @FXML
    private void handleClear() {
        firstName.clear();
        lastName.clear();
        nic.clear();
        dob.setValue(null);
        gender.setValue(null);
        lecturerImageView.setImage(null);
        selectedFile = null;

        contactNumber.clear();
        email.clear();
        emergencyContact.clear();
        district.setValue(null);
        address.clear();

        employeeId.clear();
        department.setValue(null);
        lecturerType.setValue(null);
        appointmentDate.setValue(null);
        specialization.clear();
        experienceYears.clear();
        degrees.clear();

        status.setValue(null);
        linkedUsername.clear();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}