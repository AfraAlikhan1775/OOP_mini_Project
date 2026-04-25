package com.model.Lecturerr;

public class LecturerAllCourseResultRow {

    private String regNo;
    private String studentName;
    private String courseId;
    private String courseName;

    private double quiz1;
    private double quiz2;
    private double quiz3;
    private double assignmentMark;
    private double midExam;
    private double caMark;
    private double finalTheory;
    private double finalPractical;
    private double examContribution;
    private double totalMark;
    private double attendancePercentage;

    private String eligibility;
    private String grade;
    private String status;

    public LecturerAllCourseResultRow(
            String regNo,
            String studentName,
            String courseId,
            String courseName,
            double quiz1,
            double quiz2,
            double quiz3,
            double assignmentMark,
            double midExam,
            double caMark,
            double finalTheory,
            double finalPractical,
            double examContribution,
            double totalMark,
            double attendancePercentage,
            String eligibility,
            String grade,
            String status
    ) {
        this.regNo = regNo;
        this.studentName = studentName;
        this.courseId = courseId;
        this.courseName = courseName;
        this.quiz1 = quiz1;
        this.quiz2 = quiz2;
        this.quiz3 = quiz3;
        this.assignmentMark = assignmentMark;
        this.midExam = midExam;
        this.caMark = caMark;
        this.finalTheory = finalTheory;
        this.finalPractical = finalPractical;
        this.examContribution = examContribution;
        this.totalMark = totalMark;
        this.attendancePercentage = attendancePercentage;
        this.eligibility = eligibility;
        this.grade = grade;
        this.status = status;
    }

    public String getRegNo() { return regNo; }
    public String getStudentName() { return studentName; }
    public String getCourseId() { return courseId; }
    public String getCourseName() { return courseName; }
    public double getQuiz1() { return quiz1; }
    public double getQuiz2() { return quiz2; }
    public double getQuiz3() { return quiz3; }
    public double getAssignmentMark() { return assignmentMark; }
    public double getMidExam() { return midExam; }
    public double getCaMark() { return caMark; }
    public double getFinalTheory() { return finalTheory; }
    public double getFinalPractical() { return finalPractical; }
    public double getExamContribution() { return examContribution; }
    public double getTotalMark() { return totalMark; }
    public double getAttendancePercentage() { return attendancePercentage; }
    public String getEligibility() { return eligibility; }
    public String getGrade() { return grade; }
    public String getStatus() { return status; }
}