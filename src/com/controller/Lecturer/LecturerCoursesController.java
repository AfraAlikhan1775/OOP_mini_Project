package com.controller.Lecturer;

import com.dao.Lecturer.LecturerCourseDAO;
import com.model.admin.Course;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.util.List;

public class LecturerCoursesController {

    @FXML
    private VBox courseCardContainer;

    private String lecturerEmpId;

    private final LecturerCourseDAO lecturerCourseDAO = new LecturerCourseDAO();

    public void setLecturerEmpId(String lecturerEmpId) {
        this.lecturerEmpId = lecturerEmpId;
        System.out.println("Courses page lecturerEmpId = " + lecturerEmpId);
        loadCourses();
    }

    private void loadCourses() {
        if (courseCardContainer == null) {
            System.out.println("courseCardContainer is null");
            return;
        }

        if (lecturerEmpId == null || lecturerEmpId.isBlank()) {
            System.out.println("lecturerEmpId is null or blank");
            return;
        }

        List<Course> courses = lecturerCourseDAO.getCoursesByCoordinator(lecturerEmpId);
        System.out.println("Found courses = " + courses.size());

        courseCardContainer.getChildren().clear();

        if (courses.isEmpty()) {
            Label empty = new Label("No courses assigned to this lecturer.");
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
        card.setStyle("-fx-background-color:white; -fx-background-radius:16; -fx-border-color:#d9e2ec; -fx-border-radius:16; -fx-padding:20;");
        card.setPrefHeight(130);

        VBox details = new VBox(8);

        Label title = new Label(course.getCourseName());
        title.setStyle("-fx-font-size:18px; -fx-font-weight:bold; -fx-text-fill:#0b1f36;");

        Label id = new Label("Course ID: " + course.getCourseId());
        Label info = new Label("Year " + course.getYear() + " - Semester " + course.getSemester());
        Label dep = new Label("Department: " + course.getDepartment());
        Label coordinator = new Label("Coordinator ID: " + course.getCoordinator());

        details.getChildren().addAll(title, id, dep, info, coordinator);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button viewBtn = new Button("View");
        viewBtn.setStyle("-fx-background-color:#0b1f36; -fx-text-fill:white;");

        Button removeBtn = new Button("Remove");
        removeBtn.setStyle("-fx-background-color:#c62828; -fx-text-fill:white;");

        VBox buttons = new VBox(10, viewBtn, removeBtn);
        buttons.setAlignment(Pos.CENTER_RIGHT);

        card.getChildren().addAll(details, spacer, buttons);
        return card;
    }
}