package com.model;

import java.time.LocalDate;

/**
 * Abstract class representing an Employee
 * Extends Person and adds employee-specific properties
 * Demonstrates: Inheritance, Polymorphism
 */
public abstract class Employee extends Person {

    private String employeeId;
    private String department;
    private LocalDate appointmentDate;
    private String status;

    /**
     * Constructor for Employee
     */
    public Employee(String firstName, String lastName, String nic, LocalDate dateOfBirth,
                   String gender, String imagePath, String email, String phone,
                   String address, String district, String employeeId, String department,
                   LocalDate appointmentDate, String status) {
        super(firstName, lastName, nic, dateOfBirth, gender, imagePath, email, phone, address, district);
        this.employeeId = employeeId;
        this.department = department;
        this.appointmentDate = appointmentDate;
        this.status = status;
    }

    /**
     * No-argument constructor
     */
    public Employee() {
        super();
    }

    /**
     * Polymorphic method - returns employee-specific contact info
     */
    @Override
    public String getContactInfo() {
        return super.getContactInfo() + " | Emp ID: " + employeeId + " | Dept: " + department;
    }

    /**
     * Abstract method to be implemented by specific employee types
     */
    public abstract String getEmployeeType();

    // Getters and Setters
    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public LocalDate getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(LocalDate appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

