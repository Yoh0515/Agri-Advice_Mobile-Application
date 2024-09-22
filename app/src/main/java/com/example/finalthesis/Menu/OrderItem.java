package com.example.finalthesis.Menu;

public class OrderItem {

    private String date;
    private String image;
    private String title;
    private String unit;
    private String vendor;
    private String buyer;
    private String isConfirm;
    private String recieved;
    private String key;

    public OrderItem() {
    }

    public OrderItem(String date, String image, String title, String unit, String vendor, String buyer, String isConfirm, String recieved) {
        this.date = date;
        this.image = image;
        this.title = title;
        this.unit = unit;
        this.vendor = vendor;
        this.buyer = buyer;
        this.isConfirm = isConfirm;
        this.recieved = recieved;
    }

    public String getRecieved() {
        return recieved;
    }

    public void setRecieved(String recieved) {
        this.recieved = recieved;
    }

    public String getIsConfirm() {
        return isConfirm;
    }

    public void setIsConfirm(String isConfirm) {
        this.isConfirm = isConfirm;
    }

    public String getBuyer() {
        return buyer;
    }

    public void setBuyer(String buyer) {
        this.buyer = buyer;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }
}
