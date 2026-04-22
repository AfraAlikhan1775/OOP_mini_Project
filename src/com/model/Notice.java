package com.model;

import java.sql.Timestamp;

public class Notice {
    private int id;
    private String title;
    private String description;
    private String pdfName;
    private byte[] pdfData;
    private String roleTarget;
    private String batchTarget;
    private String departmentTarget;
    private Timestamp createdAt;

    public Notice() {
    }

    public Notice(String title, String description, String pdfName, byte[] pdfData,
                  String roleTarget, String batchTarget, String departmentTarget) {
        this.title = title;
        this.description = description;
        this.pdfName = pdfName;
        this.pdfData = pdfData;
        this.roleTarget = roleTarget;
        this.batchTarget = batchTarget;
        this.departmentTarget = departmentTarget;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPdfName() {
        return pdfName;
    }

    public void setPdfName(String pdfName) {
        this.pdfName = pdfName;
    }

    public byte[] getPdfData() {
        return pdfData;
    }

    public void setPdfData(byte[] pdfData) {
        this.pdfData = pdfData;
    }

    public String getRoleTarget() {
        return roleTarget;
    }

    public void setRoleTarget(String roleTarget) {
        this.roleTarget = roleTarget;
    }

    public String getBatchTarget() {
        return batchTarget;
    }

    public void setBatchTarget(String batchTarget) {
        this.batchTarget = batchTarget;
    }

    public String getDepartmentTarget() {
        return departmentTarget;
    }

    public void setDepartmentTarget(String departmentTarget) {
        this.departmentTarget = departmentTarget;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}