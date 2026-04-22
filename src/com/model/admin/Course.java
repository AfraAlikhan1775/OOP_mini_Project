package com.model.admin;

public class Course {
    private int id;
    private String department;
    private String year;
    private String semester;
    private String courseId;
    private String courseName;
    private String coordinator;
    private int credits;
    private String imagePath;
    private String status;

    public Course() {
    }

    public Course(String department, String year, String semester, String courseId,
                  String courseName, String coordinator, int credits,
                  String imagePath, String status) {
        this.department = department;
        this.year = year;
        this.semester = semester;
        this.courseId = courseId;
        this.courseName = courseName;
        this.coordinator = coordinator;
        this.credits = credits;
        this.imagePath = imagePath;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getCoordinator() {
        return coordinator;
    }

    public void setCoordinator(String coordinator) {
        this.coordinator = coordinator;
    }

    public int getCredits() {
        return credits;
    }

    public void setCredits(int credits) {
        this.credits = credits;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}