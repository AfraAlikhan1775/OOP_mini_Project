package com.model.admin;

public class TimetableSessionInput {

    private String subject;
    private String dayName;
    private String startTime;
    private String endTime;
    private String lecturer;
    private String room;
    private String sessionType;

    public TimetableSessionInput(String subject, String dayName, String startTime,
                                 String endTime, String lecturer, String room, String sessionType) {
        this.subject = subject;
        this.dayName = dayName;
        this.startTime = startTime;
        this.endTime = endTime;
        this.lecturer = lecturer;
        this.room = room;
        this.sessionType = sessionType;
    }

    public String getSubject() {
        return subject;
    }

    public String getDayName() {
        return dayName;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public String getTime() {
        return startTime + " - " + endTime;
    }

    public String getLecturer() {
        return lecturer;
    }

    public String getRoom() {
        return room;
    }

    public String getSessionType() {
        return sessionType;
    }
}