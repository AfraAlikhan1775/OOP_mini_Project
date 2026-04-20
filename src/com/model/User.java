package com.model;

import java.lang.String;

public class User {
    private String userName;
    private String password;
    private String role;
    private int user_id;
    private String profPic;


    public User(String userName,String role){
        this.userName = userName;
        this.role = role;
    }





    public String getProfPic() {
        return profPic;
    }

    public void setProfPic(String profPic) {
        this.profPic = profPic;
    }




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
