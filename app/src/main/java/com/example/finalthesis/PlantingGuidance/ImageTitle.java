package com.example.finalthesis.PlantingGuidance;

public class ImageTitle {

    private String pestDiseaseTitles;
    private String key;

    public ImageTitle() {
    }

    public ImageTitle(String pestDiseaseTitles, String key) {
        this.pestDiseaseTitles = pestDiseaseTitles;
        this.key = key;
    }

    public String getPestDiseaseTitles() {
        return pestDiseaseTitles;
    }

    public void setPestDiseaseTitles(String pestDiseaseTitles) {
        this.pestDiseaseTitles = pestDiseaseTitles;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
