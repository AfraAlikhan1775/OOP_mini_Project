package com.model.student;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class MedicalSession {

    private final BooleanProperty selected = new SimpleBooleanProperty(false);

    private int attendanceGroupId;
    private String courseId;
    private String sessionId;
    private String sessionName;
    private String type;
    private String attendanceDate;

    public MedicalSession(int attendanceGroupId, String courseId, String sessionId,
                          String sessionName, String type, String attendanceDate) {
        this.attendanceGroupId = attendanceGroupId;
        this.courseId = courseId;
        this.sessionId = sessionId;
        this.sessionName = sessionName;
        this.type = type;
        this.attendanceDate = attendanceDate;
    }

    public BooleanProperty selectedProperty() { return selected; }
    public boolean isSelected() { return selected.get(); }
    public void setSelected(boolean selected) { this.selected.set(selected); }

    public int getAttendanceGroupId() { return attendanceGroupId; }
    public String getCourseId() { return courseId; }
    public String getSessionId() { return sessionId; }
    public String getSessionName() { return sessionName; }
    public String getType() { return type; }
    public String getAttendanceDate() { return attendanceDate; }
}