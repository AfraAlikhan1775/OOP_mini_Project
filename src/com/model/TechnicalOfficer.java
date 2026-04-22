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

    public TechnicalOfficer() {
    }

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
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmpId() { return empId; }
    public void setEmpId(String empId) { this.empId = empId; }

    public String getNic() { return nic; }
    public void setNic(String nic) { this.nic = nic; }

    public LocalDate getDob() { return dob; }
    public void setDob(LocalDate dob) { this.dob = dob; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }

    public String getDistrict() { return district; }
    public void setDistrict(String district) { this.district = district; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }

    public String getShiftType() { return shiftType; }
    public void setShiftType(String shiftType) { this.shiftType = shiftType; }

    public String getAssignedLab() { return assignedLab; }
    public void setAssignedLab(String assignedLab) { this.assignedLab = assignedLab; }
}