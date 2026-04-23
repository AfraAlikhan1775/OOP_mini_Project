package com.model.Lecturerr;

public class StudentMarkEntry {

    private int groupId;
    private String regNo;
    private double mark;

    public StudentMarkEntry(int groupId, String regNo, double mark) {
        this.groupId = groupId;
        this.regNo = regNo;
        this.mark = mark;
    }

    public String getRegNo() { return regNo; }
    public double getMark() { return mark; }
}