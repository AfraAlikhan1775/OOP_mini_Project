package com.model;

import java.time.LocalDate;

public class Session {
    private String courseId;
    private String sessionId;
    private String sessionType;
    private LocalDate sessionDate;
    private String lecturerEmpId;
    private double hours;

    public Session() {
    }

    public Session(String courseId, String sessionId, String sessionType,
                   LocalDate sessionDate, String lecturerEmpId, double hours) {
        this.courseId = courseId;
        this.sessionId = sessionId;
        this.sessionType = sessionType;
        this.sessionDate = sessionDate;
        this.lecturerEmpId = lecturerEmpId;
        this.hours = hours;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getSessionType() {
        return sessionType;
    }

    public void setSessionType(String sessionType) {
        this.sessionType = sessionType;
    }

    public LocalDate getSessionDate() {
        return sessionDate;
    }

    public void setSessionDate(LocalDate sessionDate) {
        this.sessionDate = sessionDate;
    }

    public String getLecturerEmpId() {
        return lecturerEmpId;
    }

    public void setLecturerEmpId(String lecturerEmpId) {
        this.lecturerEmpId = lecturerEmpId;
    }

    public double getHours() {
        return hours;
    }

    public void setHours(double hours) {
        this.hours = hours;
    }
}