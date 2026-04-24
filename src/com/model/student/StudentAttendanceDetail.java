package com.model.student;

public class StudentAttendanceDetail {
    private int attendanceId;
    private String date;
    private String sessionId;
    private String status;

    public StudentAttendanceDetail(int attendanceId, String date, String sessionId, String status) {
        this.attendanceId = attendanceId;
        this.date = date;
        this.sessionId = sessionId;
        this.status = status;
    }

    public int getAttendanceId() { return attendanceId; }
    public String getDate() { return date; }
    public String getSessionId() { return sessionId; }
    public String getStatus() { return status; }
}