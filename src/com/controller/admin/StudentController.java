package com.controller.admin;

import com.dao.admin.StudentDAO;
import com.model.Student;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.util.List;
import java.util.Optional;

public class StudentController {

    @FXML private VBox studentCardContainer;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> categoryComboBox;

    private final StudentDAO studentDAO = new StudentDAO();

    @FXML
    public void initialize() {
        categoryComboBox.getItems().addAll("All", "ICT", "BST", "ET");
        categoryComboBox.setValue("All");
        loadStudents();
    }

    @FXML
    private void navigateAddStudent() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/view/admin/add_student.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.showAndWait();

            loadStudents();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void navigateAddLecturer() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/view/admin/add_lecturer.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSearch() {
        String keyword = searchField.getText();

        if (keyword == null || keyword.isBlank()) {
            loadStudents();
            return;
        }

        renderStudents(studentDAO.searchStudents(keyword.trim()));
    }

    @FXML
    private void handleFilter() {
        String selected = categoryComboBox.getValue();

        if (selected == null || selected.equals("All")) {
            loadStudents();
            return;
        }

        renderStudents(studentDAO.filterByDepartment(selected));
    }

    private void loadStudents() {
        List<Student> students = studentDAO.getAllStudents();
        System.out.println("Loaded students count: " + students.size());
        renderStudents(students);
    }

    private void renderStudents(List<Student> students) {
        studentCardContainer.getChildren().clear();

        if (students == null || students.isEmpty()) {
            Label emptyLabel = new Label("No students found.");
            emptyLabel.setStyle("-fx-font-size:16px; -fx-text-fill:#555;");
            studentCardContainer.getChildren().add(emptyLabel);
            return;
        }

        for (Student student : students) {
            HBox card = createStudentCard(student);
            studentCardContainer.getChildren().add(card);
        }
    }

    private HBox createStudentCard(Student student) {
        HBox card = new HBox(20);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPrefHeight(150);
        card.setStyle(
                "-fx-background-color:white;" +
                        "-fx-background-radius:18;" +
                        "-fx-border-radius:18;" +
                        "-fx-border-color:#d9e2ec;" +
                        "-fx-padding:20;"
        );

        ImageView imageView = new ImageView();
        imageView.setFitWidth(85);
        imageView.setFitHeight(95);
        imageView.setPreserveRatio(false);

        if (student.getImagePath() != null && !student.getImagePath().isBlank()) {
            File file = new File(student.getImagePath());
            if (file.exists()) {
                imageView.setImage(new Image(file.toURI().toString()));
            }
        }

        VBox detailsBox = new VBox(10);
        detailsBox.setAlignment(Pos.CENTER_LEFT);

        Label nameLabel = new Label(student.getFirstName() + " " + student.getLastName());
        nameLabel.setStyle("-fx-font-size:18px; -fx-font-weight:bold; -fx-text-fill:#0b1f36;");

        Label regLabel = new Label("Registration No: " + valueOrEmpty(student.getRegNo()));
        regLabel.setStyle("-fx-font-size:14px; -fx-text-fill:#333333;");

        Label departmentLabel = new Label("Department: " + valueOrEmpty(student.getDepartment()));
        departmentLabel.setStyle("-fx-font-size:14px; -fx-text-fill:#333333;");

        Label degreaLabel = new Label("Degrea: " + valueOrEmpty(student.getDegrea()));
        degreaLabel.setStyle("-fx-font-size:14px; -fx-text-fill:#333333;");

        detailsBox.getChildren().addAll(nameLabel, regLabel, departmentLabel, degreaLabel);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button viewBtn = new Button("View");
        viewBtn.setPrefWidth(110);
        viewBtn.setStyle(
                "-fx-background-color:#0b1f36;" +
                        "-fx-text-fill:white;" +
                        "-fx-background-radius:6;"
        );
        viewBtn.setOnAction(e -> handleView(student));

        Button removeBtn = new Button("Remove");
        removeBtn.setPrefWidth(110);
        removeBtn.setStyle(
                "-fx-background-color:#c62828;" +
                        "-fx-text-fill:white;" +
                        "-fx-background-radius:6;"
        );
        removeBtn.setOnAction(e -> handleRemove(student));

        VBox buttonBox = new VBox(12, viewBtn, removeBtn);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);

        if (imageView.getImage() != null) {
            card.getChildren().addAll(imageView, detailsBox, spacer, buttonBox);
        } else {
            Region emptyImageSpace = new Region();
            emptyImageSpace.setPrefWidth(85);
            card.getChildren().addAll(emptyImageSpace, detailsBox, spacer, buttonBox);
        }

        return card;
    }

    private void handleView(Student student) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Student Details");
        alert.setHeaderText(student.getFirstName() + " " + student.getLastName());
        alert.setContentText(
                "Registration No: " + valueOrEmpty(student.getRegNo()) + "\n" +
                        "Department: " + valueOrEmpty(student.getDepartment()) + "\n" +
                        "Degrea: " + valueOrEmpty(student.getDegrea()) + "\n" +
                        "Year: " + valueOrEmpty(student.getYear())
        );
        alert.showAndWait();
    }

    private void handleRemove(Student student) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Remove Student");
        confirm.setHeaderText("Are you sure?");
        confirm.setContentText("Do you want to remove " + student.getRegNo() + "?");

        Optional<ButtonType> result = confirm.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            boolean deleted = studentDAO.deleteByRegNo(student.getRegNo());

            if (deleted) {
                loadStudents();
            } else {
                Alert error = new Alert(Alert.AlertType.ERROR);
                error.setTitle("Delete Failed");
                error.setHeaderText(null);
                error.setContentText("Could not remove student.");
                error.showAndWait();
            }
        }
    }

    private String valueOrEmpty(String value) {
        return value == null ? "" : value;
    }
}