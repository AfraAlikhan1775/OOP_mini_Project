package com.model;

import java.time.LocalDate;

public class Lecturer extends User{

    private String firstName;
    private String lastName;
    private String empId;
    private String nic;
    private LocalDate dob;
    private String gender;
    private String imagePath;
    private String district;

    private String email;
    private String phone;
    private String address;

    private String department;
    private String specialization;
    private String designation;
    private String qualification;

    public Lecturer(String firstName, String lastName, String empId, String nic,
                    LocalDate dob, String gender, String imagePath, String district,
                    String email, String phone, String address,
                    String department, String specialization, String designation, String qualification) {

        this.firstName = firstName;
        this.lastName = lastName;
        this.empId = empId;
        this.nic = nic;
        this.dob = dob;
        this.gender = gender;
        this.imagePath = imagePath;
        this.district = district;

        this.email = email;
        this.phone = phone;
        this.address = address;

        this.department = department;
        this.specialization = specialization;
        this.designation = designation;
        this.qualification = qualification;
    }

    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getEmpId() { return empId; }
    public String getNic() { return nic; }
    public LocalDate getDob() { return dob; }
    public String getGender() { return gender; }
    public String getImagePath() { return imagePath; }
    public String getDistrict() { return district; }

    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getAddress() { return address; }

    public String getDepartment() { return department; }
    public String getSpecialization() { return specialization; }
    public String getDesignation() { return designation; }
    public String getQualification() { return qualification; }
}