package com.model.student;

public class ExamEligibilityRow {

    private final String courseCode;
    private final String status;

    public ExamEligibilityRow(String courseCode, String status) {
        this.courseCode = courseCode;
        this.status = status;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public String getStatus() {
        return status;
    }


}