package com.example.finalthesis.Marketplace;

public class Product {
    String key;
    String title;
    String vName;
    double price;
    String imageUrl;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getvName() {
        return vName;
    }

    public void setvName(String vName) {
        this.vName = vName;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    Product(String key, String title, String vName, double price, String imageUrl) {
        this.key = key;
        this.title = title;
        this.vName = vName;
        this.price = price;
        this.imageUrl = imageUrl;
    }
}
