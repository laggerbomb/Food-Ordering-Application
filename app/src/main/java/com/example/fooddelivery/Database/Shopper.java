package com.example.fooddelivery.Database;

public class Shopper {

    String email, password;
    String  shopperId,companyName;

    public Shopper() {
    }

    public Shopper(String email, String password, String shopperId, String companyName) {
        this.email = email;
        this.password = password;
        this.shopperId = shopperId;
        this.companyName = companyName;
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

    public String getShopperId() {
        return shopperId;
    }

    public void setShopperId(String shopperId) {
        this.shopperId = shopperId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }
}
