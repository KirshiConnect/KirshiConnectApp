package com.example.dataupload.customer;

public class UserProfile {
    String name, des, price, imageUrl;

    public UserProfile() {
    }

    public UserProfile(String name, String des, String price, String imageUrl) {
        this.name = name;
        this.des = des;
        this.price = price;
        this.imageUrl = imageUrl;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getDes() {
        return des;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDes(String des) {
        this.des = des;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
