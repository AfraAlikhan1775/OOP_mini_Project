package com.controller.Lecturer;

import com.dao.Lecturer.LecturerProfileDAO;
import com.model.Lecturerr.LecturerProfileData;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

import java.io.File;

public class LecturerProfileController {

    @FXML private ImageView profileImage;

    @FXML private Label nameLabel;
    @FXML private Label empIdLabel;
    @FXML private Label nicLabel;
    @FXML private Label dobLabel;
    @FXML private Label genderLabel;
    @FXML private Label emailLabel;
    @FXML private Label phoneLabel;
    @FXML private Label addressLabel;
    @FXML private Label departmentLabel;
    @FXML private Label specializationLabel;
    @FXML private Label positionLabel;

    @FXML private PasswordField oldPasswordField;
    @FXML private PasswordField newPasswordField;
    @FXML private PasswordField confirmPasswordField;

    @FXML private Label statusLabel;

    private final LecturerProfileDAO dao = new LecturerProfileDAO();

    private String lecturerEmpId;
    private String selectedImagePath;

    public void setLecturerEmpId(String lecturerEmpId) {
        this.lecturerEmpId = lecturerEmpId;
        loadProfile();
    }

    private void loadProfile() {
        if (lecturerEmpId == null || lecturerEmpId.isBlank()) {
            showError("Lecturer session not found.");
            return;
        }

        LecturerProfileData data = dao.getProfile(lecturerEmpId);

        nameLabel.setText(data.getFullName());
        empIdLabel.setText("Employee ID: " + data.getEmpId());
        nicLabel.setText("NIC: " + data.getNic());
        dobLabel.setText("DOB: " + data.getDob());
        genderLabel.setText("Gender: " + data.getGender());
        emailLabel.setText("Email: " + data.getEmail());
        phoneLabel.setText("Phone: " + data.getPhone());
        addressLabel.setText("Address: " + data.getAddress());
        departmentLabel.setText("Department: " + data.getDepartment());
        specializationLabel.setText("Specialization: " + data.getSpecialization());
        positionLabel.setText("Position: " + data.getPosition());

        if (!setImage(data.getUserProfilePic())) {
            setDefaultImage();
        }
    }

    @FXML
    private void chooseProfilePicture() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Choose Profile Picture");

        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );

        File file = chooser.showOpenDialog(profileImage.getScene().getWindow());

        if (file != null) {
            selectedImagePath = file.getAbsolutePath();
            profileImage.setImage(new Image(file.toURI().toString()));
            showSuccess("Photo selected. Click Update Profile Picture.");
        }
    }

    @FXML
    private void updateProfilePicture() {
        if (selectedImagePath == null || selectedImagePath.isBlank()) {
            showError("Please choose a profile picture first.");
            return;
        }

        boolean updated = dao.updateUserProfilePicture(lecturerEmpId, selectedImagePath);

        if (updated) {
            showSuccess("Profile picture updated successfully.");
        } else {
            showError("Failed to update profile picture.");
        }
    }

    @FXML
    private void updatePassword() {
        String oldPassword = oldPasswordField.getText();
        String newPassword = newPasswordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (oldPassword == null || oldPassword.isBlank()
                || newPassword == null || newPassword.isBlank()
                || confirmPassword == null || confirmPassword.isBlank()) {

            showError("Please fill all password fields.");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            showError("New password and confirm password do not match.");
            return;
        }

        if (!dao.checkOldPassword(lecturerEmpId, oldPassword)) {
            showError("Old password is incorrect.");
            return;
        }

        boolean updated = dao.updatePassword(lecturerEmpId, newPassword);

        if (updated) {
            oldPasswordField.clear();
            newPasswordField.clear();
            confirmPasswordField.clear();
            showSuccess("Password updated successfully.");
        } else {
            showError("Failed to update password.");
        }
    }

    private boolean setImage(String path) {
        try {
            if (path == null || path.isBlank() || path.equals("-")) {
                return false;
            }

            File file = new File(path);

            if (file.exists()) {
                profileImage.setImage(new Image(file.toURI().toString()));
                return true;
            }

            if (path.startsWith("file:") || path.startsWith("http")) {
                profileImage.setImage(new Image(path));
                return true;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    private void setDefaultImage() {
        try {
            profileImage.setImage(new Image(
                    getClass().getResource("/com/Resources/images/icon/stu.png").toExternalForm()
            ));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showSuccess(String message) {
        statusLabel.setText(message);
        statusLabel.setStyle("-fx-text-fill:#16a34a; -fx-font-weight:bold;");
    }

    private void showError(String message) {
        statusLabel.setText(message);
        statusLabel.setStyle("-fx-text-fill:#dc2626; -fx-font-weight:bold;");
    }
}

