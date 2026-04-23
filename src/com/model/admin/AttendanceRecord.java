package com.model.admin;

public class AttendanceRecord {
    private int id;
    private int groupId;
    private String regNo;
    private String status;

    public AttendanceRecord() {
    }

    public AttendanceRecord(int groupId, String regNo, String status) {
        this.groupId = groupId;
        this.regNo = regNo;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public String getRegNo() {
        return regNo;
    }

    public void setRegNo(String regNo) {
        this.regNo = regNo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}