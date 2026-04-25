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

    private String studentProfilePic = "-";

    private String mentorId = "-";
    private String mentorName = "-";
    private String mentorEmail = "-";
    private String mentorPhone = "-";
    private String mentorDepartment = "-";
    private String mentorPhoto = "-";

    private final List<String> notices = new ArrayList<>();
    private final List<TodayTimetableRow> todayRows = new ArrayList<>();

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getRegNo() { return regNo; }
    public void setRegNo(String regNo) { this.regNo = regNo; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getCourse() { return course; }
    public void setCourse(String course) { this.course = course; }

    public String getYear() { return year; }
    public void setYear(String year) { this.year = year; }

    public String getStudentProfilePic() { return studentProfilePic; }
    public void setStudentProfilePic(String studentProfilePic) { this.studentProfilePic = studentProfilePic; }

    public String getMentorId() { return mentorId; }
    public void setMentorId(String mentorId) { this.mentorId = mentorId; }

    public String getMentorName() { return mentorName; }
    public void setMentorName(String mentorName) { this.mentorName = mentorName; }

    public String getMentorEmail() { return mentorEmail; }
    public void setMentorEmail(String mentorEmail) { this.mentorEmail = mentorEmail; }

    public String getMentorPhone() { return mentorPhone; }
    public void setMentorPhone(String mentorPhone) { this.mentorPhone = mentorPhone; }

    public String getMentorDepartment() { return mentorDepartment; }
    public void setMentorDepartment(String mentorDepartment) { this.mentorDepartment = mentorDepartment; }

    public String getMentorPhoto() { return mentorPhoto; }
    public void setMentorPhoto(String mentorPhoto) { this.mentorPhoto = mentorPhoto; }

    public List<String> getNotices() { return notices; }

    public List<TodayTimetableRow> getTodayRows() { return todayRows; }
}