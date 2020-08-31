package com.example.fooddelivery;

public class Order {

    String username, address, UID;

    public Order() {
    }

    public Order(String UID, String address, String username) {
        this.username = username;
        this.address = address;
        this.UID = UID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }
}
