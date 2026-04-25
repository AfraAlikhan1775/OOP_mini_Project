package com.model;

import java.time.LocalDate;

/**
 * Lecturer class extending Employee
 * Demonstrates: Inheritance, Polymorphism
 */
public class Lecturer extends Employee {

    private int id;
    private String lecturerType;
    private String specialization;
    private int experienceYears;

    public Lecturer() {
        super();
    }

    public Lecturer(int id, String firstName, String lastName, String employeeId, String nic,
                    LocalDate dob, String gender, String regPic,
                    String contactNumber, String email, String emergencyContact,
                    String district, String address, String department,
                    String lecturerType, LocalDate appointmentDate,
                    String specialization, int experienceYears, String status) {
        super(firstName, lastName, nic, dob, gender, regPic, email, contactNumber, address, district,
              employeeId, department, appointmentDate, status);
        this.id = id;
        this.lecturerType = lecturerType;
        this.specialization = specialization;
        this.experienceYears = experienceYears;
    }

    /**
     * Polymorphic method implementation
     */
    @Override
    public String getEmployeeType() {
        return "Lecturer - " + lecturerType;
    }

    @Override
    public String getFullName() {
        return super.getFullName() + " (" + getEmployeeId() + ")";
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLecturerType() {
        return lecturerType;
    }

    public void setLecturerType(String lecturerType) {
        this.lecturerType = lecturerType;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public int getExperienceYears() {
        return experienceYears;
    }

    public void setExperienceYears(int experienceYears) {
        this.experienceYears = experienceYears;
    }

    /**
     * Backward compatibility methods - delegate to inherited Person methods
     */
    public LocalDate getDob() {
        return getDateOfBirth();
    }

    public void setDob(LocalDate dob) {
        setDateOfBirth(dob);
    }

    public String getContactNumber() {
        return getPhone();
    }

    public void setContactNumber(String contactNumber) {
        setPhone(contactNumber);
    }

    public String getEmergencyContact() {
        return getPhone(); // Note: Consider adding emergencyContact field if needed
    }

    public void setEmergencyContact(String emergencyContact) {
        // Note: Consider adding emergencyContact field if needed
    }

    public String getRegPic() {
        return getImagePath();
    }

    public void setRegPic(String regPic) {
        setImagePath(regPic);
    }
}