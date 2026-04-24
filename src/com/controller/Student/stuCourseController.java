package com.controller.Student;

import com.dao.notes.CourseContentDAO;
import com.model.admin.Course;
import com.model.notes.CourseAnnouncement;
import com.model.notes.CourseMaterial;
import com.session.StudentSession;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;

import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

public class stuCourseController {

    @FXML
    private VBox courseContainer;

    private final CourseContentDAO contentDAO = new CourseContentDAO();

    @FXML
    public void initialize() {
        loadMyCourses();
    }

    private void loadMyCourses() {
        courseContainer.getChildren().clear();

        String regNo = StudentSession.getUsername();

        if (regNo == null || regNo.isBlank()) {
            courseContainer.getChildren().add(message("Student session not found. Please login again."));
            return;
        }

        List<Course> courses = contentDAO.getStudentRegisteredCourses(regNo);

        if (courses.isEmpty()) {
            courseContainer.getChildren().add(message("No registered courses found."));
            return;
        }

        for (Course course : courses) {
            courseContainer.getChildren().add(createCourseCard(course));
        }
    }

    private VBox createCourseCard(Course course) {
        VBox card = new VBox(14);
        card.setStyle("""
                -fx-background-color:white;
                -fx-background-radius:18;
                -fx-border-color:#d9e2ec;
                -fx-border-radius:18;
                -fx-padding:18;
                """);

        Label title = new Label(course.getCourseName());
        title.setStyle("-fx-font-size:20px; -fx-font-weight:bold; -fx-text-fill:#0b1f36;");

        Label info = new Label("Course ID: " + course.getCourseId()
                + " | Year " + course.getYear()
                + " | Semester " + course.getSemester());

        Button viewBtn = new Button("View Weekly Notes");
        viewBtn.setStyle("-fx-background-color:#0b1f36; -fx-text-fill:white; -fx-background-radius:8;");
        viewBtn.setOnAction(e -> openCourseWeeks(course));

        card.getChildren().addAll(title, info, viewBtn);
        return card;
    }

    private void openCourseWeeks(Course course) {
        courseContainer.getChildren().clear();

        Button backBtn = new Button("← Back to My Courses");
        backBtn.setStyle("-fx-background-color:#6b7280; -fx-text-fill:white; -fx-background-radius:8;");
        backBtn.setOnAction(e -> loadMyCourses());

        Label heading = new Label(course.getCourseName() + " - Weekly Content");
        heading.setStyle("-fx-font-size:23px; -fx-font-weight:bold; -fx-text-fill:#0b1f36;");

        courseContainer.getChildren().addAll(backBtn, heading);

        for (int week = 1; week <= 15; week++) {
            courseContainer.getChildren().add(createWeekBox(course.getCourseId(), week));
        }
    }

    private VBox createWeekBox(String courseId, int weekNo) {
        VBox box = new VBox(10);
        box.setStyle("""
                -fx-background-color:white;
                -fx-background-radius:16;
                -fx-border-color:#d9e2ec;
                -fx-border-radius:16;
                -fx-padding:16;
                """);

        Label weekTitle = new Label("Week " + weekNo);
        weekTitle.setStyle("-fx-font-size:18px; -fx-font-weight:bold; -fx-text-fill:#0b1f36;");

        VBox announcementBox = new VBox(8);
        VBox materialBox = new VBox(8);

        loadAnnouncements(courseId, weekNo, announcementBox);
        loadMaterials(courseId, weekNo, materialBox);

        box.getChildren().addAll(
                weekTitle,
                sectionTitle("Announcements"),
                announcementBox,
                sectionTitle("PDF Notes"),
                materialBox
        );

        return box;
    }

    private void loadAnnouncements(String courseId, int weekNo, VBox box) {
        box.getChildren().clear();

        List<CourseAnnouncement> list = contentDAO.getAnnouncements(courseId, weekNo);

        if (list.isEmpty()) {
            box.getChildren().add(message("No announcements."));
            return;
        }

        for (CourseAnnouncement a : list) {
            VBox card = new VBox(5);
            card.setStyle("-fx-background-color:#fff7ed; -fx-background-radius:10; -fx-padding:10;");

            Label text = new Label("📢 " + a.getAnnouncementText());
            text.setWrapText(true);

            Label date = new Label("Added: " + a.getCreatedAt());
            date.setStyle("-fx-text-fill:#6b7280; -fx-font-size:11px;");

            card.getChildren().addAll(text, date);
            box.getChildren().add(card);
        }
    }

    private void loadMaterials(String courseId, int weekNo, VBox box) {
        box.getChildren().clear();

        List<CourseMaterial> list = contentDAO.getMaterials(courseId, weekNo);

        if (list.isEmpty()) {
            box.getChildren().add(message("No PDF notes."));
            return;
        }

        for (CourseMaterial m : list) {
            HBox row = new HBox(12);
            row.setAlignment(Pos.CENTER_LEFT);
            row.setStyle("-fx-background-color:#f8fafc; -fx-background-radius:10; -fx-padding:10;");

            Label label = new Label("📄 " + m.getTitle() + " (" + m.getFileName() + ")");
            label.setStyle("-fx-font-weight:bold;");

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            Button openBtn = new Button("Open PDF");
            openBtn.setStyle("-fx-background-color:#14532d; -fx-text-fill:white; -fx-background-radius:8;");
            openBtn.setOnAction(e -> openPdf(m.getId()));

            row.getChildren().addAll(label, spacer, openBtn);
            box.getChildren().add(row);
        }
    }

    private void openPdf(int id) {
        try {
            byte[] data = contentDAO.getPdfFile(id);
            String fileName = contentDAO.getPdfFileName(id);

            if (data == null) {
                alert("PDF file not found.");
                return;
            }

            File temp = new File(System.getProperty("java.io.tmpdir"), fileName);

            try (FileOutputStream fos = new FileOutputStream(temp)) {
                fos.write(data);
            }

            Desktop.getDesktop().open(temp);

        } catch (Exception e) {
            e.printStackTrace();
            alert("Cannot open PDF.");
        }
    }

    private Label sectionTitle(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-font-size:15px; -fx-font-weight:bold; -fx-text-fill:#0b1f36;");
        return label;
    }

    private Label message(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-text-fill:#6b7280;");
        return label;
    }

    private void alert(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}