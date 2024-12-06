package com.example.dataupload.Models;

public class MyCartModel {
    private String productName;
    private long totalPrice;
    private long totalQuantity;
    private String currentDate;
    private String currentTime;
    private String ownerUserId;

    // Default constructor (needed for Firebase)
    public MyCartModel() {
    }

    // Constructor
    public MyCartModel(String productName, long totalPrice, long totalQuantity, String currentDate, String currentTime) {
        this.productName = productName;
        this.totalPrice = totalPrice;
        this.totalQuantity = totalQuantity;
        this.currentDate = currentDate;
        this.currentTime = currentTime;
    }

    // Getters and Setters
    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public long getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(int totalPrice) {
        this.totalPrice = totalPrice;
    }

    public void setOwnerUserId(String ownerUserId) {
        this.ownerUserId = ownerUserId;
    }

    public String getOwnerUserId() {
        return ownerUserId;
    }

    public long getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(int totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public String getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(String currentDate) {
        this.currentDate = currentDate;
    }

    public String getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(String currentTime) {
        this.currentTime = currentTime;
    }
}
