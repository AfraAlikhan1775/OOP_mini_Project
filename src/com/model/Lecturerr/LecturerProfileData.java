package com.model.Lecturerr;

public class LecturerProfileData {

    private String empId = "-";
    private String firstName = "-";
    private String lastName = "-";
    private String nic = "-";
    private String dob = "-";
    private String gender = "-";
    private String email = "-";
    private String phone = "-";
    private String address = "-";
    private String department = "-";
    private String specialization = "-";
    private String position = "-";
    private String academicPhoto = "-";
    private String userProfilePic = "-";

    public String getEmpId() { return empId; }
    public void setEmpId(String empId) { this.empId = value(empId); }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = value(firstName); }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = value(lastName); }

    public String getNic() { return nic; }
    public void setNic(String nic) { this.nic = value(nic); }

    public String getDob() { return dob; }
    public void setDob(String dob) { this.dob = value(dob); }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = value(gender); }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = value(email); }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = value(phone); }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = value(address); }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = value(department); }

    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) { this.specialization = value(specialization); }

    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = value(position); }

    public String getAcademicPhoto() { return academicPhoto; }
    public void setAcademicPhoto(String academicPhoto) { this.academicPhoto = value(academicPhoto); }

    public String getUserProfilePic() { return userProfilePic; }
    public void setUserProfilePic(String userProfilePic) { this.userProfilePic = value(userProfilePic); }

    public String getFullName() {
        return (firstName + " " + lastName).trim();
    }

    private String value(String text) {
        return text == null || text.isBlank() ? "-" : text.trim();
    }
}