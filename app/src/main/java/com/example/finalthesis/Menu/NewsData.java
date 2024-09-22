package com.example.finalthesis.Menu;

import java.util.List;

public class NewsData {
    private String title;
    private String key;
    private String uploadDate;
    private String uploadTime;
    private String username;
    private String descrip;
    private List<String> imageUrls;

    public String getDescrip() {
        return descrip;
    }

    public void setDescrip(String descrip) {
        this.descrip = descrip;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(String uploadDate) {
        this.uploadDate = uploadDate;
    }

    public String getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(String uploadTime) {
        this.uploadTime = uploadTime;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public List<String> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

    public NewsData(String descrip,String title, String key, String uploadDate, String uploadTime, String username, List<String> imageUrls) {
        this.title = title;
        this.descrip = descrip;
        this.key = key;
        this.uploadDate = uploadDate;
        this.uploadTime = uploadTime;
        this.username = username;
        this.imageUrls = imageUrls;
    }

    public NewsData() {

    }
}
