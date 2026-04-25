package com.controller.Student;

import com.dao.student.StudentProfileDAO;
import com.session.StudentSession;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

import java.io.File;

public class stuProfileController {

    @FXML private ImageView profileImage;
    @FXML private PasswordField oldPasswordField;
    @FXML private PasswordField newPasswordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Label messageLabel;

    private final StudentProfileDAO dao = new StudentProfileDAO();
    private String selectedImagePath;

    @FXML
    public void initialize() {
        loadProfilePicture();
    }

    private void loadProfilePicture() {
        String username = StudentSession.getUsername();
        String path = dao.getProfilePicture(username);

        if (path != null && !path.isBlank()) {
            File file = new File(path);
            if (file.exists()) {
                profileImage.setImage(new Image(file.toURI().toString()));
            }
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
        }
    }

    @FXML
    private void updateProfilePicture() {
        if (selectedImagePath == null || selectedImagePath.isBlank()) {
            showMessage("Please choose a profile picture first.");
            return;
        }

        String username = StudentSession.getUsername();

        boolean updated = dao.updateProfilePicture(username, selectedImagePath);

        if (updated) {
            showMessage("Profile picture updated successfully.");
        } else {
            showMessage("Failed to update profile picture.");
        }
    }

    @FXML
    private void updatePassword() {
        String username = StudentSession.getUsername();

        String oldPassword = oldPasswordField.getText();
        String newPassword = newPasswordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (oldPassword.isBlank() || newPassword.isBlank() || confirmPassword.isBlank()) {
            showMessage("Please fill all password fields.");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            showMessage("New password and confirm password do not match.");
            return;
        }

        if (!dao.checkOldPassword(username, oldPassword)) {
            showMessage("Old password is incorrect.");
            return;
        }

        boolean updated = dao.updatePassword(username, newPassword);

        if (updated) {
            oldPasswordField.clear();
            newPasswordField.clear();
            confirmPasswordField.clear();
            showMessage("Password updated successfully.");
        } else {
            showMessage("Failed to update password.");
        }
    }

    private void showMessage(String msg) {
        messageLabel.setText(msg);
    }
}