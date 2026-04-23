package com.model;

import java.sql.Timestamp;

public class CourseRegistration {
    private String regNo;
    private String courseId;
    private String semester;
    private String academicYear;
    private Timestamp registeredAt;

    public CourseRegistration() {
    }

    public CourseRegistration(String regNo, String courseId, String semester, String academicYear) {
        this.regNo = regNo;
        this.courseId = courseId;
        this.semester = semester;
        this.academicYear = academicYear;
    }

    public String getRegNo() {
        return regNo;
    }

    public void setRegNo(String regNo) {
        this.regNo = regNo;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public String getAcademicYear() {
        return academicYear;
    }

    public void setAcademicYear(String academicYear) {
        this.academicYear = academicYear;
    }

    public Timestamp getRegisteredAt() {
        return registeredAt;
    }

    public void setRegisteredAt(Timestamp registeredAt) {
        this.registeredAt = registeredAt;
    }
}