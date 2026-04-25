package com.model;

/**
 * User class implementing IAuthenticated
 * Demonstrates: Interface Implementation, Encapsulation
 */
public class User implements IAuthenticated {

    private int userId;
    private String userName;
    private String password;
    private String role;
    private String profPic;

    public User() {
    }

    public User(String userName, String password, String role, String profPic) {
        this.userName = userName;
        this.password = password;
        this.role = role;
        this.profPic = profPic;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getProfPic() {
        return profPic;
    }

    public void setProfPic(String profPic) {
        this.profPic = profPic;
    }

    /**
     * Interface implementation - IAuthenticated
     * Delegates to getUserName() for consistency
     */
    @Override
    public String getUsername() {
        return getUserName();
    }
}