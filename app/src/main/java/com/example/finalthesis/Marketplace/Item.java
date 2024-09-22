package com.example.finalthesis.Marketplace;

import android.widget.TextView;

public class Item {

    private String image;
    private String title;
    private String unit;
    private String vendor;
    private String date;
    private String key;


    public Item() {
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

    public Item(String image, String title, String unit, String vendor, String date) {
        this.image = image;
        this.title = title;
        this.unit = unit;
        this.vendor = vendor;
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

    public void addView(TextView titleView) {
    }
}
