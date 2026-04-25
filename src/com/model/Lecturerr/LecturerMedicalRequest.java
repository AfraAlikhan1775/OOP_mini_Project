package com.model.Lecturerr;

public class LecturerMedicalRequest {

    private final int medicalId;
    private final String regNo;
    private final String studentName;
    private final String medicalFor;
    private final String courseId;
    private final String sessionId;
    private final String sessionName;
    private final String type;
    private final String attendanceDate;
    private final String examType;
    private final String examDate;
    private final String startDate;
    private final String endDate;
    private final String reason;
    private final String filePath;
    private final String status;
    private final String submittedAt;

    public LecturerMedicalRequest(
            int medicalId,
            String regNo,
            String studentName,
            String medicalFor,
            String courseId,
            String sessionId,
            String sessionName,
            String type,
            String attendanceDate,
            String examType,
            String examDate,
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
        this.medicalFor = medicalFor;
        this.courseId = courseId;
        this.sessionId = sessionId;
        this.sessionName = sessionName;
        this.type = type;
        this.attendanceDate = attendanceDate;
        this.examType = examType;
        this.examDate = examDate;
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
    public String getMedicalFor() { return medicalFor; }
    public String getCourseId() { return courseId; }
    public String getSessionId() { return sessionId; }
    public String getSessionName() { return sessionName; }
    public String getType() { return type; }
    public String getAttendanceDate() { return attendanceDate; }
    public String getExamType() { return examType; }
    public String getExamDate() { return examDate; }
    public String getStartDate() { return startDate; }
    public String getEndDate() { return endDate; }
    public String getReason() { return reason; }
    public String getFilePath() { return filePath; }
    public String getStatus() { return status; }
    public String getSubmittedAt() { return submittedAt; }

    public String getDisplayType() {
        if ("EXAM".equalsIgnoreCase(medicalFor)) {
            return examType == null ? "EXAM" : examType;
        }
        return type;
    }

    public String getDisplayDate() {
        if ("EXAM".equalsIgnoreCase(medicalFor)) {
            return examDate;
        }
        return attendanceDate;
    }

    public String getDisplaySession() {
        if ("EXAM".equalsIgnoreCase(medicalFor)) {
            return examType;
        }
        return sessionName;
    }
}