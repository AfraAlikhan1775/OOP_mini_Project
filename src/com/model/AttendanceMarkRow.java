package com.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class AttendanceMarkRow {
    private final StringProperty regNo = new SimpleStringProperty();
    private final StringProperty studentName = new SimpleStringProperty();
    private final StringProperty status = new SimpleStringProperty("Present");

    public AttendanceMarkRow(String regNo, String studentName, String status) {
        this.regNo.set(regNo);
        this.studentName.set(studentName);
        this.status.set(status);
    }

    public String getRegNo() {
        return regNo.get();
    }

    public StringProperty regNoProperty() {
        return regNo;
    }

    public String getStudentName() {
        return studentName.get();
    }

    public StringProperty studentNameProperty() {
        return studentName;
    }

    public String getStatus() {
        return status.get();
    }

    public void setStatus(String value) {
        status.set(value);
    }

    public StringProperty statusProperty() {
        return status;
    }
}