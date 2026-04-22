package com.controller.admin;

import com.dao.admin.LecturerDAO;
import com.model.Lecturer;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ViewLecturerController {

    @FXML private Label firstName;
    @FXML private Label lastName;
    @FXML private Label nic;
    @FXML private Label dobLabel;
    @FXML private Label gender;
    @FXML private ImageView lecturerImageView;

    @FXML private Label contactNumber;
    @FXML private Label email;
    @FXML private Label emergencyContact;
    @FXML private Label district;
    @FXML private TextArea address;

    @FXML private Label employeeId;
    @FXML private Label department;
    @FXML private Label lecturerType;
    @FXML private Label appointmentDateLabel;
    @FXML private Label specialization;
    @FXML private Label experienceYears;
    @FXML private TextArea degrees;

    @FXML private Label status;
    @FXML private Label linkedUsername;

    private Lecturer lecturer;
    private final LecturerDAO lecturerDAO = new LecturerDAO();

    public void setLecturer(Lecturer lecturer) {
        this.lecturer = lecturer;
        fillData(lecturer);
    }

    public void loadLecturer(String empId) {
        Lecturer foundLecturer = lecturerDAO.getLecturerByEmpId(empId);
        if (foundLecturer != null) {
            this.lecturer = foundLecturer;
            fillData(foundLecturer);
        }
    }

    private void fillData(Lecturer lecturer) {
        firstName.setText(valueOrEmpty(lecturer.getFirstName()));
        lastName.setText(valueOrEmpty(lecturer.getLastName()));
        nic.setText(valueOrEmpty(lecturer.getNic()));
        dobLabel.setText(formatDate(lecturer.getDob()));
        gender.setText(valueOrEmpty(lecturer.getGender()));

        contactNumber.setText(valueOrEmpty(lecturer.getContactNumber()));
        email.setText(valueOrEmpty(lecturer.getEmail()));
        emergencyContact.setText(valueOrEmpty(lecturer.getEmergencyContact()));
        district.setText(valueOrEmpty(lecturer.getDistrict()));
        address.setText(valueOrEmpty(lecturer.getAddress()));

        employeeId.setText(valueOrEmpty(lecturer.getEmployeeId()));
        department.setText(valueOrEmpty(lecturer.getDepartment()));
        lecturerType.setText(valueOrEmpty(lecturer.getLecturerType()));
        appointmentDateLabel.setText(formatDate(lecturer.getAppointmentDate()));
        specialization.setText(valueOrEmpty(lecturer.getSpecialization()));
        experienceYears.setText(String.valueOf(lecturer.getExperienceYears()));
        status.setText(valueOrEmpty(lecturer.getStatus()));
        linkedUsername.setText(valueOrEmpty(lecturer.getEmployeeId()));

        List<String> lecturerDegrees = lecturerDAO.getLecturerDegrees(lecturer.getEmployeeId());
        degrees.setText(String.join("\n", lecturerDegrees));

        if (lecturer.getRegPic() != null && !lecturer.getRegPic().isBlank()) {
            File file = new File(lecturer.getRegPic());
            if (file.exists()) {
                lecturerImageView.setImage(new Image(file.toURI().toString()));
            }
        }
    }

//    @FXML
//    private void handleUpdate() {
//        try {
//            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/view/admin/update_lecturer.fxml"));
//            Parent root = loader.load();
//
//            UpdateLecturerController controller = loader.getController();
//            controller.loadLecturer(employeeId.getText().trim());
//
//            Stage stage = new Stage();
//            stage.setScene(new Scene(root));
//            stage.setTitle("Update Lecturer");
//            stage.show();
//
//            Stage currentStage = (Stage) firstName.getScene().getWindow();
//            currentStage.close();
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    @FXML
    private void handleBack() {
        Stage stage = (Stage) firstName.getScene().getWindow();
        stage.close();
    }

    private String valueOrEmpty(String value) {
        return value == null ? "" : value;
    }

    private String formatDate(LocalDate date) {
        if (date == null) {
            return "";
        }
        return date.format(DateTimeFormatter.ofPattern("dd MMM yyyy"));
    }
}