package com.example.finalthesis.PlantingGuidance;

public class PlantData3 {

    private String iconUrl;
    private String title;
    private String key;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public PlantData3(String iconUrl, String title, String key) {
        this.iconUrl = iconUrl;
        this.title = title;
        this.key = key;
    }

    public PlantData3(){

    }
}
