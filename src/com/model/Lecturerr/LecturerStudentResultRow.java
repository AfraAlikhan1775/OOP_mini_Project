package com.model.Lecturerr;

import java.util.HashMap;
import java.util.Map;

public class LecturerStudentResultRow {

    private final String regNo;
    private final String studentName;
    private String sgpa = "N/A";
    private String cgpa = "N/A";

    private final Map<String, String> courseValues = new HashMap<>();

    public LecturerStudentResultRow(String regNo, String studentName) {
        this.regNo = regNo;
        this.studentName = studentName;
    }

    public String getRegNo() {
        return regNo;
    }

    public String getStudentName() {
        return studentName;
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

    public Map<String, String> getCourseValues() {
        return courseValues;
    }

    public void setCourseValue(String courseId, String value) {
        courseValues.put(courseId, value);
    }
}