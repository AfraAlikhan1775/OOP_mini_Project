package com.model.student;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class TodayTimetableRow {

    private final StringProperty time = new SimpleStringProperty("");
    private final StringProperty subject = new SimpleStringProperty("");
    private final StringProperty lecturer = new SimpleStringProperty("");
    private final StringProperty room = new SimpleStringProperty("");
    private final StringProperty type = new SimpleStringProperty("");

    public TodayTimetableRow() {
    }

    public TodayTimetableRow(String time, String subject, String lecturer, String room, String type) {
        this.time.set(time);
        this.subject.set(subject);
        this.lecturer.set(lecturer);
        this.room.set(room);
        this.type.set(type);
    }

    public String getTime() {
        return time.get();
    }

    public void setTime(String value) {
        time.set(value);
    }

    public StringProperty timeProperty() {
        return time;
    }

    public String getSubject() {
        return subject.get();
    }

    public void setSubject(String value) {
        subject.set(value);
    }

    public StringProperty subjectProperty() {
        return subject;
    }

    public String getLecturer() {
        return lecturer.get();
    }

    public void setLecturer(String value) {
        lecturer.set(value);
    }

    public StringProperty lecturerProperty() {
        return lecturer;
    }

    public String getRoom() {
        return room.get();
    }

    public void setRoom(String value) {
        room.set(value);
    }

    public StringProperty roomProperty() {
        return room;
    }

    public String getType() {
        return type.get();
    }

    public void setType(String value) {
        type.set(value);
    }

    public StringProperty typeProperty() {
        return type;
    }
}