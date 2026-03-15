package com.model;

import java.lang.String;

public class User {
    private String userName;
    private String password;
    private String role;

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
}
