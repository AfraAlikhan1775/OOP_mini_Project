package com.model.notes;

public class CourseNote {
    private int id;
    private String courseId;
    private String courseName;
    private String lecturerEmpId;
    private String title;
    private String noteText;
    private String createdAt;

    public CourseNote() {}

    public CourseNote(int id, String courseId, String courseName, String lecturerEmpId,
                      String title, String noteText, String createdAt) {
        this.id = id;
        this.courseId = courseId;
        this.courseName = courseName;
        this.lecturerEmpId = lecturerEmpId;
        this.title = title;
        this.noteText = noteText;
        this.createdAt = createdAt;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getCourseId() { return courseId; }
    public void setCourseId(String courseId) { this.courseId = courseId; }

    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }

    public String getLecturerEmpId() { return lecturerEmpId; }
    public void setLecturerEmpId(String lecturerEmpId) { this.lecturerEmpId = lecturerEmpId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getNoteText() { return noteText; }
    public void setNoteText(String noteText) { this.noteText = noteText; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}