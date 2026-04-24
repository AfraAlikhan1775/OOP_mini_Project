package com.controller.Lecturer;

import com.dao.Lecturer.LecturerCourseDAO;
import com.dao.notes.CourseContentDAO;
import com.model.admin.Course;
import com.model.notes.CourseAnnouncement;
import com.model.notes.CourseMaterial;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;

import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

public class LecturerCoursesController {

    @FXML
    private VBox courseCardContainer;

    private String lecturerEmpId;

    private final LecturerCourseDAO lecturerCourseDAO = new LecturerCourseDAO();
    private final CourseContentDAO contentDAO = new CourseContentDAO();

    public void setLecturerEmpId(String lecturerEmpId) {
        this.lecturerEmpId = lecturerEmpId;
        loadCourses();
    }

    private void loadCourses() {
        courseCardContainer.getChildren().clear();

        if (lecturerEmpId == null || lecturerEmpId.isBlank()) {
            courseCardContainer.getChildren().add(message("Lecturer ID not found. Please login again."));
            return;
        }

        List<Course> courses = lecturerCourseDAO.getCoursesByCoordinator(lecturerEmpId);

        if (courses.isEmpty()) {
            courseCardContainer.getChildren().add(message("No courses assigned to this lecturer."));
            return;
        }

        for (Course course : courses) {
            courseCardContainer.getChildren().add(createCourseCard(course));
        }
    }

    private HBox createCourseCard(Course course) {
        HBox card = new HBox(20);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setStyle("""
                -fx-background-color:white;
                -fx-background-radius:18;
                -fx-border-color:#d9e2ec;
                -fx-border-radius:18;
                -fx-padding:20;
                """);

        VBox details = new VBox(7);

        Label title = new Label(course.getCourseName());
        title.setStyle("-fx-font-size:19px; -fx-font-weight:bold; -fx-text-fill:#0b1f36;");

        Label id = new Label("Course ID: " + course.getCourseId());
        Label dep = new Label("Department: " + course.getDepartment());
        Label yearSem = new Label("Year " + course.getYear() + " | Semester " + course.getSemester());
        Label credit = new Label("Credits: " + course.getCredits());

        details.getChildren().addAll(title, id, dep, yearSem, credit);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button viewBtn = new Button("View Weekly Content");
        viewBtn.setStyle("""
                -fx-background-color:#0b1f36;
                -fx-text-fill:white;
                -fx-background-radius:10;
                -fx-padding:10 16;
                """);
        viewBtn.setOnAction(e -> openWeeklyContent(course));

        card.getChildren().addAll(details, spacer, viewBtn);
        return card;
    }

    private void openWeeklyContent(Course course) {
        courseCardContainer.getChildren().clear();

        Button backBtn = new Button("← Back to My Courses");
        backBtn.setStyle("-fx-background-color:#6b7280; -fx-text-fill:white; -fx-background-radius:8; -fx-padding:8 14;");
        backBtn.setOnAction(e -> loadCourses());

        Label heading = new Label(course.getCourseName() + " - Weekly Content");
        heading.setStyle("-fx-font-size:23px; -fx-font-weight:bold; -fx-text-fill:#0b1f36;");

        Label sub = new Label("Upload PDF notes and add text announcements week by week.");
        sub.setStyle("-fx-text-fill:#607080;");

        courseCardContainer.getChildren().addAll(backBtn, heading, sub);

        for (int week = 1; week <= 15; week++) {
            courseCardContainer.getChildren().add(createWeekBox(course, week));
        }
    }

    private VBox createWeekBox(Course course, int weekNo) {
        VBox box = new VBox(12);
        box.setStyle("""
                -fx-background-color:white;
                -fx-background-radius:18;
                -fx-border-color:#d9e2ec;
                -fx-border-radius:18;
                -fx-padding:18;
                """);

        Label weekTitle = new Label("Week " + weekNo);
        weekTitle.setStyle("-fx-font-size:20px; -fx-font-weight:bold; -fx-text-fill:#0b1f36;");

        TextField pdfTitle = new TextField();
        pdfTitle.setPromptText("PDF title, example: Week " + weekNo + " Lecture Note");

        Button uploadPdfBtn = new Button("Upload PDF Note");
        uploadPdfBtn.setStyle("-fx-background-color:#14532d; -fx-text-fill:white; -fx-background-radius:8;");

        TextArea announcementArea = new TextArea();
        announcementArea.setPromptText("Type announcement for Week " + weekNo);
        announcementArea.setPrefRowCount(3);
        announcementArea.setWrapText(true);

        Button addAnnouncementBtn = new Button("Add Announcement");
        addAnnouncementBtn.setStyle("-fx-background-color:#0b1f36; -fx-text-fill:white; -fx-background-radius:8;");

        VBox materialsBox = new VBox(8);
        VBox announcementsBox = new VBox(8);

        uploadPdfBtn.setOnAction(e -> {
            String title = pdfTitle.getText().trim();

            if (title.isEmpty()) {
                alert(Alert.AlertType.WARNING, "Validation", "Please enter PDF title.");
                return;
            }

            FileChooser chooser = new FileChooser();
            chooser.setTitle("Select PDF Note");
            chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));

            File file = chooser.showOpenDialog(courseCardContainer.getScene().getWindow());

            if (file == null) {
                return;
            }

            boolean saved = contentDAO.uploadPdf(course.getCourseId(), weekNo, title, file, lecturerEmpId);

            if (saved) {
                pdfTitle.clear();
                loadMaterials(course.getCourseId(), weekNo, materialsBox);
                alert(Alert.AlertType.INFORMATION, "Success", "PDF note uploaded successfully.");
            } else {
                alert(Alert.AlertType.ERROR, "Error", "PDF note was not uploaded.");
            }
        });

        addAnnouncementBtn.setOnAction(e -> {
            String text = announcementArea.getText().trim();

            if (text.isEmpty()) {
                alert(Alert.AlertType.WARNING, "Validation", "Please type announcement.");
                return;
            }

            boolean saved = contentDAO.addAnnouncement(course.getCourseId(), weekNo, text, lecturerEmpId);

            if (saved) {
                announcementArea.clear();
                loadAnnouncements(course.getCourseId(), weekNo, announcementsBox);
                alert(Alert.AlertType.INFORMATION, "Success", "Announcement added successfully.");
            } else {
                alert(Alert.AlertType.ERROR, "Error", "Announcement was not saved.");
            }
        });

        Label pdfLabel = sectionTitle("PDF Notes");
        Label annLabel = sectionTitle("Announcements");

        loadMaterials(course.getCourseId(), weekNo, materialsBox);
        loadAnnouncements(course.getCourseId(), weekNo, announcementsBox);

        box.getChildren().addAll(
                weekTitle,
                pdfTitle,
                uploadPdfBtn,
                pdfLabel,
                materialsBox,
                announcementArea,
                addAnnouncementBtn,
                annLabel,
                announcementsBox
        );

        return box;
    }

    private void loadMaterials(String courseId, int weekNo, VBox materialsBox) {
        materialsBox.getChildren().clear();

        List<CourseMaterial> list = contentDAO.getMaterials(courseId, weekNo);

        if (list.isEmpty()) {
            materialsBox.getChildren().add(message("No PDF notes uploaded."));
            return;
        }

        for (CourseMaterial m : list) {
            HBox row = new HBox(12);
            row.setAlignment(Pos.CENTER_LEFT);
            row.setStyle("-fx-background-color:#f8fafc; -fx-background-radius:10; -fx-padding:10;");

            Label label = new Label("📄 " + m.getTitle() + " (" + m.getFileName() + ")");
            label.setStyle("-fx-font-weight:bold; -fx-text-fill:#1f2937;");

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            Button openBtn = new Button("Open");
            openBtn.setOnAction(e -> openPdf(m.getId()));

            Button deleteBtn = new Button("Delete");
            deleteBtn.setStyle("-fx-background-color:#c62828; -fx-text-fill:white;");
            deleteBtn.setOnAction(e -> {
                contentDAO.deleteMaterial(m.getId(), lecturerEmpId);
                loadMaterials(courseId, weekNo, materialsBox);
            });

            row.getChildren().addAll(label, spacer, openBtn, deleteBtn);
            materialsBox.getChildren().add(row);
        }
    }

    private void loadAnnouncements(String courseId, int weekNo, VBox announcementsBox) {
        announcementsBox.getChildren().clear();

        List<CourseAnnouncement> list = contentDAO.getAnnouncements(courseId, weekNo);

        if (list.isEmpty()) {
            announcementsBox.getChildren().add(message("No announcements added."));
            return;
        }

        for (CourseAnnouncement a : list) {
            VBox card = new VBox(5);
            card.setStyle("-fx-background-color:#fff7ed; -fx-background-radius:10; -fx-padding:10;");

            Label text = new Label("📢 " + a.getAnnouncementText());
            text.setWrapText(true);

            Label date = new Label("Added: " + a.getCreatedAt());
            date.setStyle("-fx-text-fill:#6b7280; -fx-font-size:11px;");

            Button deleteBtn = new Button("Delete");
            deleteBtn.setStyle("-fx-background-color:#c62828; -fx-text-fill:white;");
            deleteBtn.setOnAction(e -> {
                contentDAO.deleteAnnouncement(a.getId(), lecturerEmpId);
                loadAnnouncements(courseId, weekNo, announcementsBox);
            });

            card.getChildren().addAll(text, date, deleteBtn);
            announcementsBox.getChildren().add(card);
        }
    }

    private void openPdf(int id) {
        try {
            byte[] data = contentDAO.getPdfFile(id);
            String fileName = contentDAO.getPdfFileName(id);

            if (data == null) {
                alert(Alert.AlertType.ERROR, "Error", "PDF file not found.");
                return;
            }

            File temp = new File(System.getProperty("java.io.tmpdir"), fileName);

            try (FileOutputStream fos = new FileOutputStream(temp)) {
                fos.write(data);
            }

            Desktop.getDesktop().open(temp);

        } catch (Exception e) {
            e.printStackTrace();
            alert(Alert.AlertType.ERROR, "Error", "Cannot open PDF.");
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

    private void alert(Alert.AlertType type, String title, String msg) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}