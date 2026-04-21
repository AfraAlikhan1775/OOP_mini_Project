package com.model;

import java.time.LocalDate;

public class TechnicalOfficer {

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
    private String position;
    private String shiftType;
    private String assignedLab;

    public TechnicalOfficer(String firstName, String lastName, String empId, String nic,
                            LocalDate dob, String gender, String imagePath, String district,
                            String email, String phone, String address,
                            String department, String position, String shiftType, String assignedLab) {

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
        this.position = position;
        this.shiftType = shiftType;
        this.assignedLab = assignedLab;
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
    public String getPosition() { return position; }
    public String getShiftType() { return shiftType; }
    public String getAssignedLab() { return assignedLab; }
}