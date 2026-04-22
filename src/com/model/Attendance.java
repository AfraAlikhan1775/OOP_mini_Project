package com.model;

import java.time.LocalDate;

public class Attendance {

    private int attendanceId;
    private String studentId;
    private String courseCode;
    private LocalDate attendanceDate;
    private String status;
    private String markedBy;
    private String remarks;

    public Attendance() {
    }

    public Attendance(String studentId, String courseCode, LocalDate attendanceDate,
                      String status, String markedBy, String remarks) {
        this.studentId = studentId;
        this.courseCode = courseCode;
        this.attendanceDate = attendanceDate;
        this.status = status;
        this.markedBy = markedBy;
        this.remarks = remarks;
    }

    public Attendance(int attendanceId, String studentId, String courseCode,
                      LocalDate attendanceDate, String status, String markedBy, String remarks) {
        this.attendanceId = attendanceId;
        this.studentId = studentId;
        this.courseCode = courseCode;
        this.attendanceDate = attendanceDate;
        this.status = status;
        this.markedBy = markedBy;
        this.remarks = remarks;
    }

    public int getAttendanceId() { return attendanceId; }
    public void setAttendanceId(int attendanceId) { this.attendanceId = attendanceId; }

    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    public String getCourseCode() { return courseCode; }
    public void setCourseCode(String courseCode) { this.courseCode = courseCode; }

    public LocalDate getAttendanceDate() { return attendanceDate; }
    public void setAttendanceDate(LocalDate attendanceDate) { this.attendanceDate = attendanceDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getMarkedBy() { return markedBy; }
    public void setMarkedBy(String markedBy) { this.markedBy = markedBy; }

    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }

    @Override
    public String toString() {
        return "Attendance{" +
                "attendanceId=" + attendanceId +
                ", studentId='" + studentId + '\'' +
                ", courseCode='" + courseCode + '\'' +
                ", attendanceDate=" + attendanceDate +
                ", status='" + status + '\'' +
                ", markedBy='" + markedBy + '\'' +
                ", remarks='" + remarks + '\'' +
                '}';
    }
}
