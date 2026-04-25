package com.controller.admin;

import com.dao.admin.TechnicalOfficerDAO;
import com.dao.admin.UserDAO;
import com.model.TechnicalOfficer;
import com.model.User;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.Optional;

public class AddTechnicalOfficerController {

    @FXML private TextField firstName;
    @FXML private TextField lastName;
    @FXML private TextField empId;
    @FXML private TextField nic;
    @FXML private DatePicker dob;

    @FXML private RadioButton male;
    @FXML private RadioButton female;
    @FXML private ToggleGroup genderGroup;

    @FXML private ComboBox<String> district;
    @FXML private ImageView officerImageView;

    @FXML private TextField email;
    @FXML private TextField phone;
    @FXML private TextArea address;

    @FXML private ComboBox<String> department;
    @FXML private TextField position;
    @FXML private ComboBox<String> shiftType;
    @FXML private TextField assignedLab;

    @FXML private Button register;
    @FXML private Button clear;

    private File selectedFile;
    private boolean updateMode = false;
    private TechnicalOfficer existingOfficer;

    private final TechnicalOfficerDAO technicalOfficerDAO = new TechnicalOfficerDAO();
    private final UserDAO userDAO = new UserDAO();

    public void setUpdateMode(TechnicalOfficer officer) {
        this.updateMode = true;
        this.existingOfficer = officer;

        register.setText("Finish");
        clear.setText("Cancel");

        empId.setDisable(true);

        firstName.setText(value(officer.getFirstName()));
        lastName.setText(value(officer.getLastName()));
        empId.setText(value(officer.getEmpId()));
        nic.setText(value(officer.getNic()));
        dob.setValue(officer.getDob());

        if ("Male".equalsIgnoreCase(officer.getGender())) {
            male.setSelected(true);
        } else if ("Female".equalsIgnoreCase(officer.getGender())) {
            female.setSelected(true);
        }

        district.setValue(officer.getDistrict());
        email.setText(value(officer.getEmail()));
        phone.setText(value(officer.getPhone()));
        address.setText(value(officer.getAddress()));
        department.setValue(officer.getDepartment());
        position.setText(value(officer.getPosition()));
        shiftType.setValue(officer.getShiftType());
        assignedLab.setText(value(officer.getAssignedLab()));

        if (officer.getImagePath() != null && !officer.getImagePath().isBlank()) {
            File file = new File(officer.getImagePath());
            if (file.exists()) {
                officerImageView.setImage(new Image(file.toURI().toString()));
            }
        }
    }

    @FXML
    private void uploadImage() {
        FileChooser fc = new FileChooser();
        fc.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg")
        );

        File file = fc.showOpenDialog(officerImageView.getScene().getWindow());

        if (file != null) {
            selectedFile = file;
            officerImageView.setImage(new Image(file.toURI().toString()));
        }
    }

    @FXML
    private void register() {
        if (updateMode) {
            updateTechnicalOfficer();
        } else {
            addTechnicalOfficer();
        }
    }

    private void addTechnicalOfficer() {
        String employeeId = empId.getText().trim();

        if (!validateInputs(employeeId)) return;

        if (technicalOfficerDAO.existsByEmpId(employeeId)) {
            showAlert(Alert.AlertType.ERROR, "Duplicate Error", "Employee ID already exists.");
            return;
        }

        if (userDAO.existsByUsername(employeeId)) {
            showAlert(Alert.AlertType.ERROR, "Duplicate Error", "Username already exists.");
            return;
        }

        String imagePath = selectedFile != null ? selectedFile.getAbsolutePath() : null;

        TechnicalOfficer officer = buildOfficer(employeeId, imagePath);

        User user = new User(employeeId, "12345", "Technical Officer", imagePath);

        boolean userSaved = userDAO.saveUser(user);
        if (!userSaved) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to create user account.");
            return;
        }

        boolean officerSaved = technicalOfficerDAO.saveTechnicalOfficer(officer);

        if (officerSaved) {
            showAlert(Alert.AlertType.INFORMATION, "Success",
                    "Technical Officer registered successfully.\nUsername: " + employeeId + "\nPassword: 12345");
            handleClear();
        } else {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to save technical officer details.");
        }
    }

    private void updateTechnicalOfficer() {
        String employeeId = existingOfficer.getEmpId();

        if (!validateInputs(employeeId)) return;

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Update Technical Officer");
        confirm.setHeaderText(null);
        confirm.setContentText("Are you sure you want to update this technical officer?");

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isEmpty() || result.get() != ButtonType.OK) return;

        String imagePath;
        if (selectedFile != null) {
            imagePath = selectedFile.getAbsolutePath();
        } else {
            imagePath = existingOfficer.getImagePath();
        }

        TechnicalOfficer officer = buildOfficer(employeeId, imagePath);

        boolean updated = technicalOfficerDAO.updateTechnicalOfficer(officer);

        if (updated) {
            showAlert(Alert.AlertType.INFORMATION, "Success", "Technical Officer updated successfully.");
            closeWindow();
        } else {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to update technical officer.");
        }
    }

    private TechnicalOfficer buildOfficer(String employeeId, String imagePath) {
        String gender = null;

        if (male.isSelected()) {
            gender = "Male";
        } else if (female.isSelected()) {
            gender = "Female";
        }

        return new TechnicalOfficer(
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
                position.getText().trim(),
                shiftType.getValue(),
                assignedLab.getText().trim()
        );
    }

    private boolean validateInputs(String employeeId) {
        if (employeeId == null || employeeId.trim().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Employee ID is required.");
            return false;
        }

        if (firstName.getText().trim().isEmpty() || lastName.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "First name and last name are required.");
            return false;
        }

        if (department.getValue() == null) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Department is required.");
            return false;
        }

        if (shiftType.getValue() == null) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Shift type is required.");
            return false;
        }

        return true;
    }

    @FXML
    private void handleClear() {
        if (updateMode) {
            closeWindow();
            return;
        }

        firstName.clear();
        lastName.clear();
        empId.clear();
        nic.clear();
        dob.setValue(null);

        if (genderGroup != null) {
            genderGroup.selectToggle(null);
        }

        district.setValue(null);
        officerImageView.setImage(null);
        selectedFile = null;

        email.clear();
        phone.clear();
        address.clear();

        department.setValue(null);
        position.clear();
        shiftType.setValue(null);
        assignedLab.clear();
    }

    private void closeWindow() {
        Stage stage = (Stage) firstName.getScene().getWindow();
        stage.close();
    }

    private String value(String text) {
        return text == null ? "" : text;
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}