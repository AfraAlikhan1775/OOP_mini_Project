package com.model;

public class StudentMark {
    private String courseId;
    private String regNo;
    private String studentName;

    private double quiz1;
    private double quiz2;
    private double quiz3;
    private double assignment;
    private double midExam;

    private double finalTheory;
    private double finalPractical;

    private double caMark;
    private double finalMark;

    public StudentMark() {
    }

    public StudentMark(String courseId, String regNo, String studentName) {
        this.courseId = courseId;
        this.regNo = regNo;
        this.studentName = studentName;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getRegNo() {
        return regNo;
    }

    public void setRegNo(String regNo) {
        this.regNo = regNo;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public double getQuiz1() {
        return quiz1;
    }

    public void setQuiz1(double quiz1) {
        this.quiz1 = quiz1;
    }

    public double getQuiz2() {
        return quiz2;
    }

    public void setQuiz2(double quiz2) {
        this.quiz2 = quiz2;
    }

    public double getQuiz3() {
        return quiz3;
    }

    public void setQuiz3(double quiz3) {
        this.quiz3 = quiz3;
    }

    public double getAssignment() {
        return assignment;
    }

    public void setAssignment(double assignment) {
        this.assignment = assignment;
    }

    public double getMidExam() {
        return midExam;
    }

    public void setMidExam(double midExam) {
        this.midExam = midExam;
    }

    public double getFinalTheory() {
        return finalTheory;
    }

    public void setFinalTheory(double finalTheory) {
        this.finalTheory = finalTheory;
    }

    public double getFinalPractical() {
        return finalPractical;
    }

    public void setFinalPractical(double finalPractical) {
        this.finalPractical = finalPractical;
    }

    public double getCaMark() {
        return caMark;
    }

    public void setCaMark(double caMark) {
        this.caMark = caMark;
    }

    public double getFinalMark() {
        return finalMark;
    }

    public void setFinalMark(double finalMark) {
        this.finalMark = finalMark;
    }
}