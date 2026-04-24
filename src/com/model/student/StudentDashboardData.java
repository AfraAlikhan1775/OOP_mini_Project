package com.model.student;

import java.util.ArrayList;
import java.util.List;

public class StudentDashboardData {

    private String fullName = "-";
    private String regNo = "-";
    private String email = "-";
    private String department = "-";
    private String course = "-";
    private String year = "-";
    private String mentorId = "-";

    private String attendancePercentage = "0%";
    private String courseCount = "0";
    private String medicalCount = "0";

    private final List<String> notices = new ArrayList<>();
    private final List<TodayTimetableRow> todayRows = new ArrayList<>();

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getRegNo() {
        return regNo;
    }

    public void setRegNo(String regNo) {
        this.regNo = regNo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getMentorId() {
        return mentorId;
    }

    public void setMentorId(String mentorId) {
        this.mentorId = mentorId;
    }

    public String getAttendancePercentage() {
        return attendancePercentage;
    }

    public void setAttendancePercentage(String attendancePercentage) {
        this.attendancePercentage = attendancePercentage;
    }

    public String getCourseCount() {
        return courseCount;
    }

    public void setCourseCount(String courseCount) {
        this.courseCount = courseCount;
    }

    public String getMedicalCount() {
        return medicalCount;
    }

    public void setMedicalCount(String medicalCount) {
        this.medicalCount = medicalCount;
    }

    public List<String> getNotices() {
        return notices;
    }

    public List<TodayTimetableRow> getTodayRows() {
        return todayRows;
    }
}