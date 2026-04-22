package com.model;

import java.time.LocalDate;

public class Student {

    private String firstName;
    private String lastName;
    private String regNo;
    private String nic;
    private LocalDate dob;
    private String gender;
    private String imagePath;
    private String district;

    private String email;
    private String phone;
    private String address;

    private String department;
    private String degrea;
    private String year;
    private String mentor;

    private String guardianName;
    private String guardianPhone;
    private String guardianRelationship;

    public Student(String firstName, String lastName, String regNo, String nic,
                   LocalDate dob, String gender, String imagePath, String district,
                   String email, String phone, String address,
                   String department, String course, String year, String mentor,
                   String guardianName, String guardianPhone, String guardianRelationship) {

        this.firstName = firstName;
        this.lastName = lastName;
        this.regNo = regNo;
        this.nic = nic;
        this.dob = dob;
        this.gender = gender;
        this.imagePath = imagePath;
        this.district = district;

        this.email = email;
        this.phone = phone;
        this.address = address;

        this.department = department;
        this.degrea = degrea;
        this.year = year;
        this.mentor = mentor;

        this.guardianName = guardianName;
        this.guardianPhone = guardianPhone;
        this.guardianRelationship = guardianRelationship;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getRegNo() {
        return regNo;
    }

    public String getNic() {
        return nic;
    }

    public LocalDate getDob() {
        return dob;
    }

    public String getGender() {
        return gender;
    }

    public String getImagePath() {
        return imagePath;
    }

    public String getDistrict() {
        return district;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getAddress() {
        return address;
    }

    public String getDepartment() {
        return department;
    }

    public String getDegrea() {
        return degrea;
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
}