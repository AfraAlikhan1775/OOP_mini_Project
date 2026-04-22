package com.controller.admin;

import com.dao.admin.CourseDAO;
import com.model.admin.Course;
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

public class CourseController {

    @FXML private VBox courseCardContainer;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> categoryComboBox;

    private final CourseDAO courseDAO = new CourseDAO();

    @FXML
    public void initialize() {
        categoryComboBox.getItems().addAll("All", "ICT", "BST", "ET");
        categoryComboBox.setValue("All");
        loadCourses();
    }

    @FXML
    private void navigateAddCourse() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/view/admin/add_course.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.showAndWait();

            loadCourses();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSearch() {
        String keyword = searchField.getText();

        if (keyword == null || keyword.isBlank()) {
            loadCourses();
            return;
        }

        renderCourses(courseDAO.searchCourses(keyword.trim()));
    }

    @FXML
    private void handleFilter() {
        String selected = categoryComboBox.getValue();

        if (selected == null || selected.equals("All")) {
            loadCourses();
            return;
        }

        renderCourses(courseDAO.filterByDepartment(selected));
    }

    private void loadCourses() {
        renderCourses(courseDAO.getAllCourses());
    }

    private void renderCourses(List<Course> courses) {
        courseCardContainer.getChildren().clear();

        if (courses == null || courses.isEmpty()) {
            Label empty = new Label("No courses found.");
            empty.setStyle("-fx-font-size:16px; -fx-text-fill:#555;");
            courseCardContainer.getChildren().add(empty);
            return;
        }

        for (Course course : courses) {
            courseCardContainer.getChildren().add(createCourseCard(course));
        }
    }

    private HBox createCourseCard(Course course) {
        HBox card = new HBox(20);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPrefHeight(150);
        card.setStyle("-fx-background-color:white; -fx-background-radius:18; -fx-border-radius:18; -fx-border-color:#d9e2ec; -fx-padding:20;");

        ImageView imageView = new ImageView();
        imageView.setFitWidth(85);
        imageView.setFitHeight(95);
        imageView.setPreserveRatio(false);

        if (course.getImagePath() != null && !course.getImagePath().isBlank()) {
            File file = new File(course.getImagePath());
            if (file.exists()) {
                imageView.setImage(new Image(file.toURI().toString()));
            }
        }

        VBox detailsBox = new VBox(10);

        Label nameLabel = new Label(course.getCourseName());
        nameLabel.setStyle("-fx-font-size:20px; -fx-font-weight:bold; -fx-text-fill:#0b1f36;");

        Label idLabel = new Label("Course ID: " + course.getCourseId());
        Label departmentLabel = new Label("Department: " + course.getDepartment());
        Label semesterLabel = new Label("Year " + course.getYear() + " - Semester " + course.getSemester());
        Label coordinatorLabel = new Label("Coordinator ID: " + course.getCoordinator());

        detailsBox.getChildren().addAll(nameLabel, idLabel, departmentLabel, semesterLabel, coordinatorLabel);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button viewBtn = new Button("View");
        viewBtn.setPrefWidth(110);
        viewBtn.setStyle("-fx-background-color:#0b1f36; -fx-text-fill:white;");
        viewBtn.setOnAction(e -> handleView(course));

        Button removeBtn = new Button("Remove");
        removeBtn.setPrefWidth(110);
        removeBtn.setStyle("-fx-background-color:#c62828; -fx-text-fill:white;");
        removeBtn.setOnAction(e -> handleRemove(course));

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

    private void handleView(Course course) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Course Details");
        alert.setHeaderText(course.getCourseName());
        alert.setContentText(
                "Course ID: " + course.getCourseId() + "\n" +
                        "Department: " + course.getDepartment() + "\n" +
                        "Year: " + course.getYear() + "\n" +
                        "Semester: " + course.getSemester() + "\n" +
                        "Coordinator ID: " + course.getCoordinator() + "\n" +
                        "Credits: " + course.getCredits() + "\n" +
                        "Status: " + course.getStatus()
        );
        alert.showAndWait();
    }

    private void handleRemove(Course course) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Remove Course");
        confirm.setHeaderText("Are you sure?");
        confirm.setContentText("Do you want to remove " + course.getCourseId() + "?");

        Optional<ButtonType> result = confirm.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            boolean deleted = courseDAO.deleteByCourseId(course.getCourseId());
            if (deleted) {
                loadCourses();
            } else {
                Alert error = new Alert(Alert.AlertType.ERROR);
                error.setTitle("Delete Failed");
                error.setHeaderText(null);
                error.setContentText("Could not remove course.");
                error.showAndWait();
            }
        }
    }
}