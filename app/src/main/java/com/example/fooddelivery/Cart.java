package com.example.fooddelivery;

public class Cart {

    String foodName, userID;
    String quantity;
    String totalPrice, pushID;
    String companyName;

    public Cart() {
    }

    public Cart(String foodName, String userID, String quantity, String totalPrice, String pushID,String companyName) {
        this.foodName = foodName;
        this.userID = userID;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
        this.pushID = pushID;
        this.companyName= companyName;
    }

    public String getPushID() {
        return pushID;
    }

    public void setPushID(String pushID) {
        this.pushID = pushID;
    }

    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(String totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }
}
