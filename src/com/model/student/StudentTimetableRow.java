package com.model.student;

public class StudentTimetableRow {
    private String day;
    private String time;
    private String subject;
    private String lecturer;
    private String room;
    private String type;

    public StudentTimetableRow(String day, String time, String subject,
                               String lecturer, String room, String type) {
        this.day = day;
        this.time = time;
        this.subject = subject;
        this.lecturer = lecturer;
        this.room = room;
        this.type = type;
    }

    public String getDay() { return day; }
    public String getTime() { return time; }
    public String getSubject() { return subject; }
    public String getLecturer() { return lecturer; }
    public String getRoom() { return room; }
    public String getType() { return type; }
}