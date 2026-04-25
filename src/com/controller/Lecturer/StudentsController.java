package com.controller.Lecturer;

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
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.File;
import java.util.List;

public class StudentsController {

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
        renderStudents(studentDAO.getAllStudents());
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
            studentCardContainer.getChildren().add(createStudentCard(student));
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

        Label nameLabel = new Label(value(student.getFirstName()) + " " + value(student.getLastName()));
        nameLabel.setStyle("-fx-font-size:18px; -fx-font-weight:bold; -fx-text-fill:#0b1f36;");

        Label regLabel = new Label("Registration No: " + value(student.getRegNo()));
        Label departmentLabel = new Label("Department: " + value(student.getDepartment()));
        Label degreeLabel = new Label("Degree: " + value(student.getDegrea()));
        Label yearLabel = new Label("Year: " + value(student.getYear()));

        regLabel.setStyle("-fx-font-size:14px; -fx-text-fill:#333333;");
        departmentLabel.setStyle("-fx-font-size:14px; -fx-text-fill:#333333;");
        degreeLabel.setStyle("-fx-font-size:14px; -fx-text-fill:#333333;");
        yearLabel.setStyle("-fx-font-size:14px; -fx-text-fill:#333333;");

        detailsBox.getChildren().addAll(nameLabel, regLabel, departmentLabel, degreeLabel, yearLabel);

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

        VBox buttonBox = new VBox(12, viewBtn);
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
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/view/Lec_N/view_student.fxml"));
            Parent root = loader.load();

            ViewStudentController controller = loader.getController();
            controller.setStudent(student);

            Stage stage = new Stage();
            stage.setTitle("Student Details");
            stage.setScene(new Scene(root));
            stage.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Cannot open student details.");
        }
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