package com.example.fooddelivery.Database;

public class FoodList {

    String foodName, foodPrice, foodUrl,id;

    public FoodList(String foodName, String foodPrice, String id) {
        this.foodName = foodName;
        this.foodPrice = foodPrice;
        this.id= id;
    }

    public FoodList() {
        //Non-argument constructor
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public String getFoodPrice() {
        return foodPrice;
    }

    public void setFoodPrice(String foodPrice) {
        this.foodPrice = foodPrice;
    }

    public String getFoodUrl() {
        return foodUrl;
    }

    public void setFoodUrl(String foodUrl) {
        this.foodUrl = foodUrl;
    }
}
