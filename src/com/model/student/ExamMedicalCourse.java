package com.model.student;

public class ExamMedicalCourse {
    private final String courseId;
    private final String courseName;

    public ExamMedicalCourse(String courseId, String courseName) {
        this.courseId = courseId;
        this.courseName = courseName;
    }

    public String getCourseId() {
        return courseId;
    }

    public String getCourseName() {
        return courseName;
    }

    @Override
    public String toString() {
        return courseId + " - " + courseName;
    }
}