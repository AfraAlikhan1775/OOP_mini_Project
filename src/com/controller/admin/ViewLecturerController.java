package com.controller.admin;

import com.dao.admin.LecturerDAO;
import com.dao.admin.UserDAO;
import com.model.Lecturer;
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

public class ViewLecturerController {

    @FXML private ImageView profileImage;

    @FXML private Label firstNameLabel;
    @FXML private Label lastNameLabel;
    @FXML private Label employeeIdLabel;
    @FXML private Label nicLabel;
    @FXML private Label dobLabel;
    @FXML private Label genderLabel;
    @FXML private Label districtLabel;
    @FXML private Label emailLabel;
    @FXML private Label phoneLabel;
    @FXML private Label emergencyContactLabel;
    @FXML private Label addressLabel;
    @FXML private Label departmentLabel;
    @FXML private Label lecturerTypeLabel;
    @FXML private Label appointmentDateLabel;
    @FXML private Label specializationLabel;
    @FXML private Label experienceLabel;
    @FXML private Label statusLabel;
    @FXML private Label degreesLabel;

    private Lecturer selectedLecturer;
    private final LecturerDAO lecturerDAO = new LecturerDAO();

    public void setLecturer(Lecturer lecturer) {
        this.selectedLecturer = lecturer;

        firstNameLabel.setText(value(lecturer.getFirstName()));
        lastNameLabel.setText(value(lecturer.getLastName()));
        employeeIdLabel.setText(value(lecturer.getEmployeeId()));
        nicLabel.setText(value(lecturer.getNic()));
        dobLabel.setText(lecturer.getDob() == null ? "" : lecturer.getDob().toString());
        genderLabel.setText(value(lecturer.getGender()));
        districtLabel.setText(value(lecturer.getDistrict()));
        emailLabel.setText(value(lecturer.getEmail()));
        phoneLabel.setText(value(lecturer.getContactNumber()));
        emergencyContactLabel.setText(value(lecturer.getEmergencyContact()));
        addressLabel.setText(value(lecturer.getAddress()));
        departmentLabel.setText(value(lecturer.getDepartment()));
        lecturerTypeLabel.setText(value(lecturer.getLecturerType()));
        appointmentDateLabel.setText(lecturer.getAppointmentDate() == null ? "" : lecturer.getAppointmentDate().toString());
        specializationLabel.setText(value(lecturer.getSpecialization()));
        experienceLabel.setText(String.valueOf(lecturer.getExperienceYears()));
        statusLabel.setText(value(lecturer.getStatus()));

        degreesLabel.setText(String.join("\n", lecturerDAO.getLecturerDegrees(lecturer.getEmployeeId())));

        if (lecturer.getRegPic() != null && !lecturer.getRegPic().isBlank()) {
            File file = new File(lecturer.getRegPic());
            if (file.exists()) {
                profileImage.setImage(new Image(file.toURI().toString()));
            }
        }
    }

    @FXML
    private void handleUpdate() {
        if (!askAdminPassword()) return;

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/view/admin/add_lecturer.fxml"));
            Parent root = loader.load();

            AddLecturerController controller = loader.getController();
            controller.setUpdateMode(selectedLecturer);

            Stage stage = new Stage();
            stage.setTitle("Update Lecturer");
            stage.setScene(new Scene(root));
            stage.showAndWait();

            closeWindow();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Cannot open update lecturer page.");
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