package com.model.Lecturerr;

public class MarksGroup {

    private int groupId;
    private String courseId;
    private String year;
    private String semester;
    private String academicYear;
    private String examType;
    private String createdBy;   // 🔥 ADD THIS

    public MarksGroup(int groupId, String courseId, String year, String semester,
                      String academicYear, String examType, String createdBy) {
        this.groupId = groupId;
        this.courseId = courseId;
        this.year = year;
        this.semester = semester;
        this.academicYear = academicYear;
        this.examType = examType;
        this.createdBy = createdBy;   // 🔥 ADD THIS
    }

    public int getGroupId() {
        return groupId;
    }

    public String getCourseId() {
        return courseId;
    }

    public String getYear() {
        return year;
    }

    public String getSemester() {
        return semester;
    }

    public String getAcademicYear() {
        return academicYear;
    }

    public String getExamType() {
        return examType;
    }

    // 🔥 ADD THIS METHOD
    public String getCreatedBy() {
        return createdBy;
    }
}