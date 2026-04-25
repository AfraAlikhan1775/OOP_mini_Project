package com.controller.admin;

import com.dao.admin.UserDAO;
import com.model.TechnicalOfficer;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.File;
import java.util.Optional;

public class ViewTechnicalOfficerController {

    @FXML private ImageView profileImage;

    @FXML private Label firstNameLabel;
    @FXML private Label lastNameLabel;
    @FXML private Label empIdLabel;
    @FXML private Label nicLabel;
    @FXML private Label dobLabel;
    @FXML private Label genderLabel;
    @FXML private Label districtLabel;
    @FXML private Label emailLabel;
    @FXML private Label phoneLabel;
    @FXML private Label addressLabel;
    @FXML private Label departmentLabel;
    @FXML private Label positionLabel;
    @FXML private Label shiftTypeLabel;
    @FXML private Label assignedLabLabel;

    private TechnicalOfficer selectedOfficer;

    public void setTechnicalOfficer(TechnicalOfficer officer) {
        this.selectedOfficer = officer;

        firstNameLabel.setText(value(officer.getFirstName()));
        lastNameLabel.setText(value(officer.getLastName()));
        empIdLabel.setText(value(officer.getEmpId()));
        nicLabel.setText(value(officer.getNic()));
        dobLabel.setText(officer.getDob() == null ? "" : officer.getDob().toString());
        genderLabel.setText(value(officer.getGender()));
        districtLabel.setText(value(officer.getDistrict()));
        emailLabel.setText(value(officer.getEmail()));
        phoneLabel.setText(value(officer.getPhone()));
        addressLabel.setText(value(officer.getAddress()));
        departmentLabel.setText(value(officer.getDepartment()));
        positionLabel.setText(value(officer.getPosition()));
        shiftTypeLabel.setText(value(officer.getShiftType()));
        assignedLabLabel.setText(value(officer.getAssignedLab()));

        if (officer.getImagePath() != null && !officer.getImagePath().isBlank()) {
            File file = new File(officer.getImagePath());
            if (file.exists()) {
                profileImage.setImage(new Image(file.toURI().toString()));
            }
        }
    }

    @FXML
    private void handleUpdate() {
        if (!askAdminPassword()) return;

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/view/admin/add_technicalofficer.fxml"));
            Parent root = loader.load();

            AddTechnicalOfficerController controller = loader.getController();
            controller.setUpdateMode(selectedOfficer);

            Stage stage = new Stage();
            stage.setTitle("Update Technical Officer");
            stage.setScene(new Scene(root));
            stage.showAndWait();

            closeWindow();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Cannot open update page.");
        }
    }

    @FXML
    private void handleClose() {
        closeWindow();
    }

    private boolean askAdminPassword() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setHeaderText("Enter Admin Password");

        Optional<String> result = dialog.showAndWait();
        if (result.isEmpty()) return false;

        UserDAO userDAO = new UserDAO();

        if (!userDAO.isAdminPasswordCorrect(result.get())) {
            showAlert(Alert.AlertType.ERROR, "Wrong Password", "Incorrect admin password.");
            return false;
        }

        return true;
    }

    private void closeWindow() {
        Stage stage = (Stage) firstNameLabel.getScene().getWindow();
        stage.close();
    }

    private String value(String text) {
        return text == null ? "" : text;
    }

    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}