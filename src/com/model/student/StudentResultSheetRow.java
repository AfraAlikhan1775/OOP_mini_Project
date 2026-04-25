package com.model.student;

import java.util.LinkedHashMap;
import java.util.Map;

public class StudentResultSheetRow {

    private String regNo;
    private Map<String, String> courseGrades = new LinkedHashMap<>();
    private String sgpa;
    private String cgpa;

    public StudentResultSheetRow(String regNo) {
        this.regNo = regNo;
    }

    public String getRegNo() {
        return regNo;
    }

    public Map<String, String> getCourseGrades() {
        return courseGrades;
    }

    public void setCourseGrade(String courseCode, String grade) {
        courseGrades.put(courseCode, grade);
    }

    public String getCourseGrade(String courseCode) {
        return courseGrades.getOrDefault(courseCode, "-");
    }

    public String getSgpa() {
        return sgpa;
    }

    public void setSgpa(String sgpa) {
        this.sgpa = sgpa;
    }

    public String getCgpa() {
        return cgpa;
    }

    public void setCgpa(String cgpa) {
        this.cgpa = cgpa;
    }
}