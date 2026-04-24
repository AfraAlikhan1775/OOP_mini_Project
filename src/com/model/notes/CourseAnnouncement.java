package com.model.notes;

public class CourseAnnouncement {
    private int id;
    private String courseId;
    private int weekNo;
    private String announcementText;
    private String createdBy;
    private String createdAt;

    public CourseAnnouncement(int id, String courseId, int weekNo,
                              String announcementText, String createdBy, String createdAt) {
        this.id = id;
        this.courseId = courseId;
        this.weekNo = weekNo;
        this.announcementText = announcementText;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
    }

    public int getId() { return id; }
    public String getCourseId() { return courseId; }
    public int getWeekNo() { return weekNo; }
    public String getAnnouncementText() { return announcementText; }
    public String getCreatedBy() { return createdBy; }
    public String getCreatedAt() { return createdAt; }
}