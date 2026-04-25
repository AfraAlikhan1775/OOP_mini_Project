package com.controller.admin;

import com.dao.UserDAO;
import com.model.Student;
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

public class ViewStudentController {

    @FXML private ImageView profileImage;

    @FXML private Label firstNameLabel;
    @FXML private Label lastNameLabel;
    @FXML private Label regNoLabel;
    @FXML private Label nicLabel;
    @FXML private Label dobLabel;
    @FXML private Label genderLabel;
    @FXML private Label districtLabel;
    @FXML private Label emailLabel;
    @FXML private Label phoneLabel;
    @FXML private Label addressLabel;
    @FXML private Label departmentLabel;
    @FXML private Label degreeLabel;
    @FXML private Label yearLabel;

    private Student selectedStudent;

    public void setStudent(Student student) {
        this.selectedStudent = student;

        firstNameLabel.setText(value(student.getFirstName()));
        lastNameLabel.setText(value(student.getLastName()));
        regNoLabel.setText(value(student.getRegNo()));
        nicLabel.setText(value(student.getNic()));
        dobLabel.setText(student.getDob() == null ? "" : student.getDob().toString());
        genderLabel.setText(value(student.getGender()));
        districtLabel.setText(value(student.getDistrict()));
        emailLabel.setText(value(student.getEmail()));
        phoneLabel.setText(value(student.getPhone()));
        addressLabel.setText(value(student.getAddress()));
        departmentLabel.setText(value(student.getDepartment()));
        degreeLabel.setText(value(student.getDegrea()));
        yearLabel.setText(value(student.getYear()));

        if (student.getImagePath() != null && !student.getImagePath().isBlank()) {
            File file = new File(student.getImagePath());
            if (file.exists()) {
                profileImage.setImage(new Image(file.toURI().toString()));
            }
        }
    }

    @FXML
    private void handleUpdate() {
        if (!askAdminPassword()) return;

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/view/admin/add_student.fxml"));
            Parent root = loader.load();

            AddStudentController controller = loader.getController();
            controller.setUpdateMode(selectedStudent);

            Stage stage = new Stage();
            stage.setTitle("Update Student");
            stage.setScene(new Scene(root));
            stage.showAndWait();

            closeWindow();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Cannot open update student page.");
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