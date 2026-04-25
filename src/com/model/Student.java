package com.model;

import java.time.LocalDate;

/**
 * Student class extending Person
 * Demonstrates: Inheritance, Encapsulation, Polymorphism
 */
public class Student extends Person {

    private String regNo;
    private String department;
    private String degree;
    private String year;
    private String mentor;

    private String guardianName;
    private String guardianPhone;
    private String guardianRelationship;

    public Student(String firstName, String lastName, String regNo, String nic,
                   LocalDate dob, String gender, String imagePath, String district,
                   String email, String phone, String address,
                   String department, String degree, String year, String mentor,
                   String guardianName, String guardianPhone, String guardianRelationship) {

        super(firstName, lastName, nic, dob, gender, imagePath, email, phone, address, district);
        this.regNo = regNo;
        this.department = department;
        this.degree = degree;
        this.year = year;
        this.mentor = mentor;

        this.guardianName = guardianName;
        this.guardianPhone = guardianPhone;
        this.guardianRelationship = guardianRelationship;
    }

    /**
     * No-argument constructor
     */
    public Student() {
        super();
    }

    /**
     * Polymorphic method override from Person
     */
    @Override
    public String getFullName() {
        return getFirstName() + " " + getLastName() + " (" + regNo + ")";
    }

    public String getRegNo() {
        return regNo;
    }

    public String getDepartment() {
        return department;
    }

    public String getDegree() {
        return degree;
    }

    public String getYear() {
        return year;
    }

    public String getMentor() {
        return mentor;
    }

    public String getGuardianName() {
        return guardianName;
    }

    public String getGuardianPhone() {
        return guardianPhone;
    }

    public String getGuardianRelationship() {
        return guardianRelationship;
    }

    /**
     * Legacy method for backward compatibility
     */
    public String getDegrea() {
        return degree;
    }

    /**
     * Backward compatibility method - delegate to inherited Person method
     */
    public LocalDate getDob() {
        return getDateOfBirth();
    }
}