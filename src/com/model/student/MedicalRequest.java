package com.model.student;

public class MedicalRequest {

    private final int medicalId;
    private final String regNo;
    private final String filePath;
    private final String startDate;
    private final String endDate;
    private final String reason;
    private final String status;
    private final String approvedBy;
    private final String submittedAt;
    private final String medicalFor;

    public MedicalRequest(int medicalId, String regNo, String filePath, String startDate,
                          String endDate, String reason, String status,
                          String approvedBy, String submittedAt, String medicalFor) {
        this.medicalId = medicalId;
        this.regNo = regNo;
        this.filePath = filePath;
        this.startDate = startDate;
        this.endDate = endDate;
        this.reason = reason;
        this.status = status;
        this.approvedBy = approvedBy;
        this.submittedAt = submittedAt;
        this.medicalFor = medicalFor;
    }

    public int getMedicalId() { return medicalId; }
    public String getRegNo() { return regNo; }
    public String getFilePath() { return filePath; }
    public String getStartDate() { return startDate; }
    public String getEndDate() { return endDate; }
    public String getReason() { return reason; }
    public String getStatus() { return status; }
    public String getApprovedBy() { return approvedBy; }
    public String getSubmittedAt() { return submittedAt; }
    public String getMedicalFor() { return medicalFor; }
}