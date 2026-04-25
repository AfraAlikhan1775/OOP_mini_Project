package com.model.Lecturerr;

public class LecturerResultCard {

    private final String department;
    private final String year;
    private final String semester;

    public LecturerResultCard(String department, String year, String semester) {
        this.department = department;
        this.year = year;
        this.semester = semester;
    }

    public String getDepartment() {
        return department;
    }

    public String getYear() {
        return year;
    }

    public String getSemester() {
        return semester;
    }
}