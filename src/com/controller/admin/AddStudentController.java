package com.controller.admin;

import com.dao.admin.StudentDAO;
import com.dao.admin.UserDAO;
import com.model.Student;
import com.model.User;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.util.Optional;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.control.Button;



import java.io.File;

public class AddStudentController {

    @FXML private TextField firstName;
    @FXML private TextField lastName;
    @FXML private TextField regNo;
    @FXML private TextField nic;
    @FXML private DatePicker dob;

    @FXML private RadioButton male;
    @FXML private RadioButton female;
    @FXML private ToggleGroup genderGroup;
    @FXML
    private Button uploadImage;


    @FXML private ComboBox<String> district;
    @FXML private ImageView studentImageView;

    @FXML private TextField email;
    @FXML private TextField phone;
    @FXML private TextArea address;

    @FXML private ComboBox<String> department;
    @FXML private ComboBox<String> degrea;
    @FXML private TextField year;
    @FXML private TextField mentor;

    @FXML private TextField guardianName;
    @FXML private TextField guardianPhone;
    @FXML private TextField guardianRelationship;

    @FXML private Button register;
    @FXML private Button clear;

    private boolean viewMode = false;
    private boolean updateMode = false;
    private Student selectedStudent;

    private File selectedFile;

    private final StudentDAO studentDAO = new StudentDAO();
    private final UserDAO userDAO = new UserDAO();

    @FXML
    private void uploadImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Image");

        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );

        File file = fileChooser.showOpenDialog(firstName.getScene().getWindow());

        if (file != null) {
            selectedFile = file;
            studentImageView.setImage(new Image(file.toURI().toString()));
        }
    }

    @FXML
    private void register() {
        String registrationNumber = regNo.getText().trim();

        if (registrationNumber.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Registration number is required.");
            return;
        }

        if (firstName.getText().trim().isEmpty() || lastName.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "First name and last name are required.");
            return;
        }

        if (studentDAO.existsByRegNo(registrationNumber)) {
            showAlert(Alert.AlertType.ERROR, "Duplicate Error", "Registration number already exists.");
            return;
        }

        if (userDAO.existsByUsername(registrationNumber)) {
            showAlert(Alert.AlertType.ERROR, "Duplicate Error", "Username already exists.");
            return;
        }

        String gender = null;
        if (male.isSelected()) {
            gender = "Male";
        } else if (female.isSelected()) {
            gender = "Female";
        }

        String imagePath = (selectedFile != null) ? selectedFile.getAbsolutePath() : null;

        Student s = new Student(
                firstName.getText().trim(),
                lastName.getText().trim(),
                registrationNumber,
                nic.getText().trim(),
                dob.getValue(),
                gender,
                imagePath,
                district.getValue(),
                email.getText().trim(),
                phone.getText().trim(),
                address.getText().trim(),
                department.getValue(),
                degrea.getValue(),
                year.getText().trim(),
                mentor.getText().trim(),
                guardianName.getText().trim(),
                guardianPhone.getText().trim(),
                guardianRelationship.getText().trim()
        );

        User u = new User(
                registrationNumber,
                "12345",
                "Student",
                imagePath
        );

        boolean userSaved = userDAO.saveUser(u);

        if (!userSaved) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to create user account.");
            return;
        }

        boolean studentSaved = studentDAO.saveStudent(s);

        if (studentSaved) {
            showAlert(Alert.AlertType.INFORMATION, "Success",
                    "Student registered successfully.\nUsername: " + registrationNumber + "\nPassword: 12345");

            Stage stage = (Stage) firstName.getScene().getWindow();
            stage.close();
        } else {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to save student details.");
        }
    }

    @FXML
    private void handleClear() {
        firstName.clear();
        lastName.clear();
        regNo.clear();
        nic.clear();
        dob.setValue(null);

        genderGroup.selectToggle(null);

        district.setValue(null);
        studentImageView.setImage(null);
        selectedFile = null;

        email.clear();
        phone.clear();
        address.clear();

        department.setValue(null);
        degrea.setValue(null);
        year.clear();
        mentor.clear();

        guardianName.clear();
        guardianPhone.clear();
        guardianRelationship.clear();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    public void setViewMode(Student student) {
        selectedStudent = student;

        fillFields(student);
        setEditable(false);

        register.setText("Update");

        register.setOnAction(e -> {
            if (!askAdminPassword()) return;
            enableUpdateMode();
        });

        clear.setText("Close");
        clear.setOnAction(e -> closeWindow());
    }

    private void enableUpdateMode() {
        setEditable(true);
        regNo.setEditable(false);

        register.setText("Finish");

        register.setOnAction(e -> finishUpdate());
    }
    private void setUpdateMode() {
        updateMode = true;
        viewMode = false;

        setEditable(true);
        regNo.setEditable(false);

        register.setText("Finish");
        clear.setText("Cancel");

        register.setOnAction(e -> finishUpdate());
        clear.setOnAction(e -> closeWindow());
    }

    private void finishUpdate() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setHeaderText("Confirm Update");
        confirm.setContentText("Are you sure to update?");

        Optional<ButtonType> result = confirm.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            Student s = collectStudent();

            if (studentDAO.updateStudent(s)) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Updated successfully");
                closeWindow();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Update failed");
            }
        }
    }
    private Student collectStudent() {
        String gender = null;
        if (male.isSelected()) gender = "Male";
        else if (female.isSelected()) gender = "Female";

        String imagePath = selectedFile != null
                ? selectedFile.getAbsolutePath()
                : selectedStudent != null ? selectedStudent.getImagePath() : null;

        return new Student(
                firstName.getText().trim(),
                lastName.getText().trim(),
                regNo.getText().trim(),
                nic.getText().trim(),
                dob.getValue(),
                gender,
                imagePath,
                district.getValue(),
                email.getText().trim(),
                phone.getText().trim(),
                address.getText().trim(),
                department.getValue(),
                degrea.getValue(),
                year.getText().trim(),
                mentor.getText().trim(),
                guardianName.getText().trim(),
                guardianPhone.getText().trim(),
                guardianRelationship.getText().trim()
        );
    }

    private void fillFields(Student s) {
        firstName.setText(s.getFirstName());
        lastName.setText(s.getLastName());
        regNo.setText(s.getRegNo());
        nic.setText(s.getNic());
        dob.setValue(s.getDob());

        if ("Male".equalsIgnoreCase(s.getGender())) male.setSelected(true);
        else if ("Female".equalsIgnoreCase(s.getGender())) female.setSelected(true);

        district.setValue(s.getDistrict());
        email.setText(s.getEmail());
        phone.setText(s.getPhone());
        address.setText(s.getAddress());
        department.setValue(s.getDepartment());
        degrea.setValue(s.getDegrea());
        year.setText(s.getYear());
        mentor.setText(s.getMentor());
        guardianName.setText(s.getGuardianName());
        guardianPhone.setText(s.getGuardianPhone());
        guardianRelationship.setText(s.getGuardianRelationship());

        if (s.getImagePath() != null) {
            File file = new File(s.getImagePath());
            if (file.exists()) {
                studentImageView.setImage(new Image(file.toURI().toString()));
            }
        }
    }

    private void setEditable(boolean value) {
        firstName.setEditable(value);
        lastName.setEditable(value);
        regNo.setEditable(value);
        nic.setEditable(value);
        dob.setDisable(!value);
        male.setDisable(!value);
        female.setDisable(!value);
        district.setDisable(!value);
        uploadImage.setDisable(!value);
        email.setEditable(value);
        phone.setEditable(value);
        address.setEditable(value);
        department.setDisable(!value);
        degrea.setDisable(!value);
        year.setEditable(value);
        mentor.setEditable(value);
        guardianName.setEditable(value);
        guardianPhone.setEditable(value);
        guardianRelationship.setEditable(value);
    }

    private boolean askAdminPassword() {
        PasswordDialog dialog = new PasswordDialog();
        dialog.setTitle("Admin Password Required");
        dialog.setHeaderText("Enter admin password");

        Optional<String> result = dialog.showAndWait();

        if (result.isEmpty()) return false;

        UserDAO userDAO = new UserDAO();

        if (!userDAO.isAdminPasswordCorrect(result.get())) {
            showAlert(Alert.AlertType.ERROR, "Wrong Password", "Admin password is incorrect.");
            return false;
        }

        return true;
    }

    private void closeWindow() {
        Stage stage = (Stage) firstName.getScene().getWindow();
        stage.close();
    }

    public void setUpdateMode(Student student) {
        selectedStudent = student;

        fillFields(student);
        setEditable(true);

        regNo.setEditable(false);
        uploadImage.setText("Choose Profile Picture");

        register.setText("Finish");
        clear.setText("Cancel");

        register.setOnAction(e -> finishUpdate());
        clear.setOnAction(e -> closeWindow());
    }
}