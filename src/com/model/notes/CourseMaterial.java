package com.model.notes;

public class CourseMaterial {
    private int id;
    private String courseId;
    private int weekNo;
    private String title;
    private String fileName;
    private String uploadedBy;
    private String uploadedAt;

    public CourseMaterial() {}

    public CourseMaterial(int id, String courseId, int weekNo, String title,
                          String fileName, String uploadedBy, String uploadedAt) {
        this.id = id;
        this.courseId = courseId;
        this.weekNo = weekNo;
        this.title = title;
        this.fileName = fileName;
        this.uploadedBy = uploadedBy;
        this.uploadedAt = uploadedAt;
    }

    public int getId() { return id; }
    public String getCourseId() { return courseId; }
    public int getWeekNo() { return weekNo; }
    public String getTitle() { return title; }
    public String getFileName() { return fileName; }
    public String getUploadedBy() { return uploadedBy; }
    public String getUploadedAt() { return uploadedAt; }
}