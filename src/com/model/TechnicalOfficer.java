package com.model;

import java.time.LocalDate;

/**
 * TechnicalOfficer class extending Employee
 * Demonstrates: Inheritance, Polymorphism
 */
public class TechnicalOfficer extends Employee {

    private String position;
    private String shiftType;
    private String assignedLab;

    public TechnicalOfficer() {
        super();
    }

    public TechnicalOfficer(String firstName, String lastName, String empId, String nic,
                            LocalDate dob, String gender, String imagePath, String district,
                            String email, String phone, String address,
                            String department, String position, String shiftType, String assignedLab) {
        super(firstName, lastName, nic, dob, gender, imagePath, email, phone, address, district,
              empId, department, null, "Active");
        this.position = position;
        this.shiftType = shiftType;
        this.assignedLab = assignedLab;
    }

    /**
     * Polymorphic method implementation
     */
    @Override
    public String getEmployeeType() {
        return "Technical Officer - " + position;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getShiftType() {
        return shiftType;
    }

    public void setShiftType(String shiftType) {
        this.shiftType = shiftType;
    }

    public String getAssignedLab() {
        return assignedLab;
    }

    public void setAssignedLab(String assignedLab) {
        this.assignedLab = assignedLab;
    }

    /**
     * Backward compatibility methods - delegate to inherited Employee/Person methods
     */
    public String getEmpId() {
        return getEmployeeId();
    }

    public void setEmpId(String empId) {
        setEmployeeId(empId);
    }

    public LocalDate getDob() {
        return getDateOfBirth();
    }

    public void setDob(LocalDate dob) {
        setDateOfBirth(dob);
    }
}