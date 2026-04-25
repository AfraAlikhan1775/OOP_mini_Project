package com.model;

import java.time.LocalDate;

/**
 * Abstract base class representing a Person
 * Demonstrates: Abstraction, Encapsulation, Interface Implementation
 */
public abstract class Person implements IProfilePicture {

    // Common properties for all persons
    private String firstName;
    private String lastName;
    private String nic;
    private LocalDate dateOfBirth;
    private String gender;
    private String imagePath;

    // Contact information
    private String email;
    private String phone;
    private String address;
    private String district;

    /**
     * Constructor for Person
     */
    public Person(String firstName, String lastName, String nic, LocalDate dateOfBirth,
                  String gender, String imagePath, String email, String phone,
                  String address, String district) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.nic = nic;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.imagePath = imagePath;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.district = district;
    }

    /**
     * No-argument constructor
     */
    public Person() {
    }

    // Polymorphic method - to be overridden in subclasses
    public String getFullName() {
        return firstName + " " + lastName;
    }

    // Polymorphic method - to be overridden in subclasses
    public String getContactInfo() {
        return "Email: " + email + " | Phone: " + phone;
    }

    /**
     * Interface implementation - IProfilePicture
     */
    @Override
    public boolean hasProfilePicture() {
        return imagePath != null && !imagePath.isEmpty();
    }

    // Getters and Setters (Encapsulation)
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

    public String getNic() {
        return nic;
    }

    public void setNic(String nic) {
        this.nic = nic;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }
}



