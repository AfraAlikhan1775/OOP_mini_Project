package com.model.admin;

import java.time.LocalDate;

public class AttendanceGroup {
    private int id;
    private int year;
    private String courseId;
    private String sessionId;
    private String type;
    private LocalDate attendanceDate;

    public AttendanceGroup() {
    }

    public AttendanceGroup(int year, String courseId, String sessionId, String type, LocalDate attendanceDate) {
        this.year = year;
        this.courseId = courseId;
        this.sessionId = sessionId;
        this.type = type;
        this.attendanceDate = attendanceDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public LocalDate getAttendanceDate() {
        return attendanceDate;
    }

    public void setAttendanceDate(LocalDate attendanceDate) {
        this.attendanceDate = attendanceDate;
    }
}