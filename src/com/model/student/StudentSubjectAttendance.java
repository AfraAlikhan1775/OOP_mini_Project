package com.model.student;

public class StudentSubjectAttendance {
    private String courseId;
    private String courseName;
    private String type;
    private int total;
    private int present;
    private int medical;
    private int rejectedMedical;
    private int absent;

    public StudentSubjectAttendance(String courseId, String courseName, String type,
                                    int total, int present, int medical,
                                    int rejectedMedical, int absent) {
        this.courseId = courseId;
        this.courseName = courseName;
        this.type = type;
        this.total = total;
        this.present = present;
        this.medical = medical;
        this.rejectedMedical = rejectedMedical;
        this.absent = absent;
    }

    public String getCourseId() { return courseId; }
    public String getCourseName() { return courseName; }
    public String getType() { return type; }
    public int getTotal() { return total; }
    public int getPresent() { return present; }
    public int getMedical() { return medical; }
    public int getRejectedMedical() { return rejectedMedical; }
    public int getAbsent() { return absent; }

    public double getPercentage() {
        if (total == 0) return 0;
        return ((present + medical) * 100.0) / total;
    }
}