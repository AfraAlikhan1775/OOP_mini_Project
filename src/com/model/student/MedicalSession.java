package com.model.student;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class MedicalSession {

    private final BooleanProperty selected = new SimpleBooleanProperty(false);

    private final int attendanceGroupId;
    private final String courseId;
    private final String sessionId;
    private final String sessionName;
    private final String type;
    private final String attendanceDate;

    private final String medicalFor;
    private final String examType;
    private final String examDate;

    public MedicalSession(int attendanceGroupId, String courseId, String sessionId,
                          String sessionName, String type, String attendanceDate) {
        this.attendanceGroupId = attendanceGroupId;
        this.courseId = courseId;
        this.sessionId = sessionId;
        this.sessionName = sessionName;
        this.type = type;
        this.attendanceDate = attendanceDate;

        this.medicalFor = "ATTENDANCE";
        this.examType = null;
        this.examDate = null;
    }

    public MedicalSession(String courseId, String examType, String examDate) {
        this.attendanceGroupId = 0;
        this.courseId = courseId;
        this.sessionId = examType;
        this.sessionName = examType;
        this.type = "EXAM";
        this.attendanceDate = examDate;

        this.medicalFor = "EXAM";
        this.examType = examType;
        this.examDate = examDate;
    }

    public BooleanProperty selectedProperty() {
        return selected;
    }

    public boolean isSelected() {
        return selected.get();
    }

    public void setSelected(boolean selected) {
        this.selected.set(selected);
    }

    public int getAttendanceGroupId() {
        return attendanceGroupId;
    }

    public String getCourseId() {
        return courseId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getSessionName() {
        return sessionName;
    }

    public String getType() {
        return type;
    }

    public String getAttendanceDate() {
        return attendanceDate;
    }

    public String getDate() {
        return attendanceDate;
    }

    public String getMedicalFor() {
        return medicalFor;
    }

    public String getExamType() {
        return examType;
    }

    public String getExamDate() {
        return examDate;
    }
}