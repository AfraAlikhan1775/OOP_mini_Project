package com.model.admin;

public class SessionItem {
    private String courseId;
    private String sessionId;
    private String type;
    private String sessionName;
    private int year;
    private int semester;

    public SessionItem() {
    }

    public SessionItem(String courseId, String sessionId, String type, String sessionName, int year, int semester) {
        this.courseId = courseId;
        this.sessionId = sessionId;
        this.type = type;
        this.sessionName = sessionName;
        this.year = year;
        this.semester = semester;
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

    public String getSessionName() {
        return sessionName;
    }

    public void setSessionName(String sessionName) {
        this.sessionName = sessionName;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getSemester() {
        return semester;
    }

    public void setSemester(int semester) {
        this.semester = semester;
    }
}