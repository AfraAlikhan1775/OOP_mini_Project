package com.model;

import java.lang.String;

public class User {
    private String userName;
    private String password;
    private String role;
    private int user_id;

    public int getUserId() {
        return user_id;
    }

    public void setUserId(int user_id) {
        this.user_id = user_id;
    }



    public void setUserName(String userName){
        this.userName = userName;
    }

    public String getUserName(){
        return userName;
    }

    public void setPassword(String password){
        this.password = password;

    }
    public String getPassword(){
        return password;
    }

    public void setRole(String role){
        this.role = role;
    }

    public String getRole(){
        return role;
    }
}
