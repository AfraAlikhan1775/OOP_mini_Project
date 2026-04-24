package com.model.Lecturerr;

public class LecturerMedicalRequest {

    private int medicalId;
    private String regNo;
    private String studentName;
    private String courseId;
    private String sessionId;
    private String sessionName;
    private String type;
    private String attendanceDate;
    private String startDate;
    private String endDate;
    private String reason;
    private String filePath;
    private String status;
    private String submittedAt;

    public LecturerMedicalRequest(
            int medicalId,
            String regNo,
            String studentName,
            String courseId,
            String sessionId,
            String sessionName,
            String type,
            String attendanceDate,
            String startDate,
            String endDate,
            String reason,
            String filePath,
            String status,
            String submittedAt
    ) {
        this.medicalId = medicalId;
        this.regNo = regNo;
        this.studentName = studentName;
        this.courseId = courseId;
        this.sessionId = sessionId;
        this.sessionName = sessionName;
        this.type = type;
        this.attendanceDate = attendanceDate;
        this.startDate = startDate;
        this.endDate = endDate;
        this.reason = reason;
        this.filePath = filePath;
        this.status = status;
        this.submittedAt = submittedAt;
    }

    public int getMedicalId() { return medicalId; }
    public String getRegNo() { return regNo; }
    public String getStudentName() { return studentName; }
    public String getCourseId() { return courseId; }
    public String getSessionId() { return sessionId; }
    public String getSessionName() { return sessionName; }
    public String getType() { return type; }
    public String getAttendanceDate() { return attendanceDate; }
    public String getStartDate() { return startDate; }
    public String getEndDate() { return endDate; }
    public String getReason() { return reason; }
    public String getFilePath() { return filePath; }
    public String getStatus() { return status; }
    public String getSubmittedAt() { return submittedAt; }
}