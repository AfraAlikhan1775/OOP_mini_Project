package com.model;

public class AttendanceEntry {
    private String courseId;
    private String sessionId;
    private String regNo;
    private String status;

    public AttendanceEntry() {
    }

    public AttendanceEntry(String courseId, String sessionId, String regNo, String status) {
        this.courseId = courseId;
        this.sessionId = sessionId;
        this.regNo = regNo;
        this.status = status;
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

    public String getRegNo() {
        return regNo;
    }

    public void setRegNo(String regNo) {
        this.regNo = regNo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}