package com.example.finalthesis.Menu;

public class NewsItem {
    String imageUrls;
    String key;

    public String getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(String imageUrls) {
        this.imageUrls = imageUrls;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public NewsItem(String imageUrls, String key) {
        this.imageUrls = imageUrls;
        this.key = key;
    }

    public NewsItem(){

    }
}
