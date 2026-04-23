package com.model;

public class CourseAssessmentScheme {
    private String courseId;
    private boolean hasTheory;
    private boolean hasPractical;

    public CourseAssessmentScheme() {
    }

    public CourseAssessmentScheme(String courseId, boolean hasTheory, boolean hasPractical) {
        this.courseId = courseId;
        this.hasTheory = hasTheory;
        this.hasPractical = hasPractical;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public boolean isHasTheory() {
        return hasTheory;
    }

    public void setHasTheory(boolean hasTheory) {
        this.hasTheory = hasTheory;
    }

    public boolean isHasPractical() {
        return hasPractical;
    }

    public void setHasPractical(boolean hasPractical) {
        this.hasPractical = hasPractical;
    }
}