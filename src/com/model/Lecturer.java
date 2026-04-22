package com.model;

import java.time.LocalDate;

public class Lecturer {

    private int id;
    private String firstName;
    private String lastName;
    private String employeeId;
    private String nic;
    private LocalDate dob;
    private String gender;
    private String regPic;

    private String contactNumber;
    private String email;
    private String emergencyContact;
    private String district;
    private String address;

    private String department;
    private String lecturerType;
    private LocalDate appointmentDate;
    private String specialization;
    private int experienceYears;
    private String status;

    public Lecturer() {
    }

    public Lecturer(int id, String firstName, String lastName, String employeeId, String nic,
                    LocalDate dob, String gender, String regPic,
                    String contactNumber, String email, String emergencyContact,
                    String district, String address, String department,
                    String lecturerType, LocalDate appointmentDate,
                    String specialization, int experienceYears, String status) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.employeeId = employeeId;
        this.nic = nic;
        this.dob = dob;
        this.gender = gender;
        this.regPic = regPic;
        this.contactNumber = contactNumber;
        this.email = email;
        this.emergencyContact = emergencyContact;
        this.district = district;
        this.address = address;
        this.department = department;
        this.lecturerType = lecturerType;
        this.appointmentDate = appointmentDate;
        this.specialization = specialization;
        this.experienceYears = experienceYears;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getNic() {
        return nic;
    }

    public void setNic(String nic) {
        this.nic = nic;
    }

    public LocalDate getDob() {
        return dob;
    }

    public void setDob(LocalDate dob) {
        this.dob = dob;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getRegPic() {
        return regPic;
    }

    public void setRegPic(String regPic) {
        this.regPic = regPic;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmergencyContact() {
        return emergencyContact;
    }

    public void setEmergencyContact(String emergencyContact) {
        this.emergencyContact = emergencyContact;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getLecturerType() {
        return lecturerType;
    }

    public void setLecturerType(String lecturerType) {
        this.lecturerType = lecturerType;
    }

    public LocalDate getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(LocalDate appointmentDate) {
        this.appointmentDate = appointmentDate;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}