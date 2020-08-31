package com.example.fooddelivery;

import com.google.firebase.auth.FirebaseUser;

public class User {

    String email, password;
    String  userId,username;

    User(){
    }

    User(String email, String password, String user,String username){
        this.email = email;
        this.password = password;
        this.userId = user;
        this.username= username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String user) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
