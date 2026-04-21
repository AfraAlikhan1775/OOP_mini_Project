package com.model;

import java.util.Arrays;

public class Medical {

    /*********
     *
     DROP TABLE IF EXISTS `medical`;
     CREATE TABLE `medical` (
     `medical_id` INT PRIMARY KEY AUTO_INCREMENT,
     `student_id` VARCHAR(15),
     `medical_data` LONGBLOB,
     `medical_start_date` DATE NOT NULL,
     `medical_end_date` DATE NOT NULL,
     `batch` VARCHAR(10)
     );
     *
     *
     */


    private int medicalId;
    private String studentId;
    private byte[] medicalData;
    private String medicalStartDate;
    private String medicalEndDate;
    private String batch;
    private String department;
    private String reason;
    private String addedBy;
    private String status;

    public Medical() {
    }

    public Medical(int medicalId, String studentId, String batch, String department, String medicalStartDate, String medicalEndDate, byte[] medicalData) {
        this.medicalId = medicalId;
        this.studentId = studentId;
        this.batch = batch;
        this.department = department;
        this.medicalStartDate = medicalStartDate;
        this.medicalEndDate = medicalEndDate;
        this.medicalData = medicalData;
    }

    public Medical(String studentId, String batch, String department, String medicalStartDate, String medicalEndDate, byte[] medicalData) {
        this.studentId = studentId;
        this.batch = batch;
        this.department = department;
        this.medicalStartDate = medicalStartDate;
        this.medicalEndDate = medicalEndDate;
        this.medicalData = medicalData;
    }

    public Medical(int medicalId, String studentId, String batch, String medicalStartDate, String medicalEndDate, byte[] medicalData) {
        this.medicalId = medicalId;
        this.studentId = studentId;
        this.batch = batch;
        this.medicalStartDate = medicalStartDate;
        this.medicalEndDate = medicalEndDate;
        this.medicalData = medicalData;
    }

    public Medical(String studentId, String batch, String department, String medicalStartDate,
                   String medicalEndDate, byte[] medicalData, String reason, String addedBy, String status) {
        this.studentId = studentId;
        this.batch = batch;
        this.department = department;
        this.medicalStartDate = medicalStartDate;
        this.medicalEndDate = medicalEndDate;
        this.medicalData = medicalData;
        this.reason = reason;
        this.addedBy = addedBy;
        this.status = status;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public int getMedicalId() {
        return medicalId;
    }

    public void setMedicalId(int medicalId) {
        this.medicalId = medicalId;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public byte[] getMedicalData() {
        return medicalData;
    }

    public void setMedicalData(byte[] medicalData) {
        this.medicalData = medicalData;
    }

    public String getMedicalStartDate() {
        return medicalStartDate;
    }

    public void setMedicalStartDate(String medicalStartDate) {
        this.medicalStartDate = medicalStartDate;
    }

    public String getMedicalEndDate() {
        return medicalEndDate;
    }

    public void setMedicalEndDate(String medicalEndDate) {
        this.medicalEndDate = medicalEndDate;
    }

    public String getBatch() {
        return batch;
    }

    public void setBatch(String batch) {
        this.batch = batch;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getAddedBy() {
        return addedBy;
    }

    public void setAddedBy(String addedBy) {
        this.addedBy = addedBy;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    @Override
    public String toString() {
        return "Medical{" +
                "medicalId=" + medicalId +
                ", studentId='" + studentId + '\'' +
                ", medicalData=" + Arrays.toString(medicalData) +
                ", medicalStartDate='" + medicalStartDate + '\'' +
                ", medicalEndDate='" + medicalEndDate + '\'' +
                ", batch='" + batch + '\'' +
                ", department='" + department + '\'' +
                ", reason='" + reason + '\'' +
                ", addedBy='" + addedBy + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
