package com.example.finalthesis.PlantingGuidance;

public class PlantData2 {

    private String iconUrl;
    private String plantIcon;
    private String plantTitle;
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

    public String getPlantIcon() {
        return plantIcon;
    }

    public void setPlantIcon(String plantIcon) {
        this.plantIcon = plantIcon;
    }

    public String getPlantTitle() {
        return plantTitle;
    }

    public void setPlantTitle(String plantTitle) {
        this.plantTitle = plantTitle;
    }

    public PlantData2(String iconUrl, String plantIcon, String plantTitle, String title) {
        this.iconUrl = iconUrl;
        this.plantIcon = plantIcon;
        this.plantTitle = plantTitle;
        this.title = title;
    }

    public PlantData2(){

    }
}
