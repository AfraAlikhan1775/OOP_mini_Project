package com.controller.techOfficerControllers;

import com.dao.admin.TechnicalOfficerDAO;
import com.model.TechnicalOfficer;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.File;

public class TOProfileController {

    @FXML private ImageView profileImage;
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField empIdField;
    @FXML private TextField nicField;
    @FXML private DatePicker dobPicker;
    @FXML private ComboBox<String> genderCombo;
    @FXML private TextField districtField;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;
    @FXML private TextArea addressField;
    @FXML private ComboBox<String> departmentCombo;
    @FXML private TextField positionField;
    @FXML private ComboBox<String> shiftCombo;
    @FXML private TextField assignedLabField;
    @FXML private Label statusLabel;

    private String empId;
    private String currentImagePath;
    private final TechnicalOfficerDAO toDAO = new TechnicalOfficerDAO();

    @FXML
    public void initialize() {
        genderCombo.getItems().addAll("Male", "Female", "Other");
        departmentCombo.getItems().addAll("ICT", "BST", "ET");
        shiftCombo.getItems().addAll("Morning", "Evening", "Night", "General");
    }

    public void setEmpId(String empId) {
        this.empId = empId;
        loadProfileData();
    }

    private void loadProfileData() {
        if (empId == null) return;

//        TechnicalOfficer to = toDAO.getTOByEmpId(empId);
//        if (to == null) {
//            statusLabel.setText("Error: Profile not found.");
//            statusLabel.setStyle("-fx-text-fill: red;");
//            return;
//        }

//        Platform.runLater(() -> {
//            firstNameField.setText(valueOrEmpty(to.getFirstName()));
//            lastNameField.setText(valueOrEmpty(to.getLastName()));
//            empIdField.setText(to.getEmpId()); // Disabled field
//            nicField.setText(valueOrEmpty(to.getNic()));
//            if (to.getDob() != null) dobPicker.setValue(to.getDob());
//            genderCombo.setValue(to.getGender());
//            districtField.setText(valueOrEmpty(to.getDistrict()));
//
//            emailField.setText(valueOrEmpty(to.getEmail()));
//            phoneField.setText(valueOrEmpty(to.getPhone()));
//            addressField.setText(valueOrEmpty(to.getAddress()));
//
//            departmentCombo.setValue(to.getDepartment());
//            positionField.setText(valueOrEmpty(to.getPosition()));
//            shiftCombo.setValue(to.getShiftType());
//            assignedLabField.setText(valueOrEmpty(to.getAssignedLab()));
//
//            currentImagePath = to.getImagePath();
//            if (currentImagePath != null && !currentImagePath.isBlank()) {
//                File file = new File(currentImagePath);
//                if (file.exists()) {
//                    profileImage.setImage(new Image(file.toURI().toString()));
//                }
//            }
//        });
    }

    @FXML
    private void handleUploadPhoto() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
        Window window = firstNameField.getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(window);

        if (selectedFile != null) {
            currentImagePath = selectedFile.getAbsolutePath();
            profileImage.setImage(new Image(selectedFile.toURI().toString()));
            statusLabel.setText("Photo selected. Remember to save changes.");
            statusLabel.setStyle("-fx-text-fill: #16a34a;");
        }
    }

    @FXML
    private void handleSave() {
        if (empId == null) return;

        TechnicalOfficer to = new TechnicalOfficer(
                firstNameField.getText(),
                lastNameField.getText(),
                empId,
                nicField.getText(),
                dobPicker.getValue(),
                genderCombo.getValue(),
                currentImagePath,
                districtField.getText(),
                emailField.getText(),
                phoneField.getText(),
                addressField.getText(),
                departmentCombo.getValue(),
                positionField.getText(),
                shiftCombo.getValue(),
                assignedLabField.getText()
        );

//        if (toDAO.updateProfile(to)) {
//            statusLabel.setText("Profile updated successfully!");
//            statusLabel.setStyle("-fx-text-fill: #16a34a;");
//        } else {
//            statusLabel.setText("Failed to update profile.");
//            statusLabel.setStyle("-fx-text-fill: red;");
//        }
    }

    private String valueOrEmpty(String value) {
        return value == null ? "" : value;
    }
}
