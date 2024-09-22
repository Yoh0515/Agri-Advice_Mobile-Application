package com.example.finalthesis.PlantingGuidance;

public class PlantData4 {

    private String plantTitle;
    private String key;
    private String plantIcon;

    public String getPlantTitle() {
        return plantTitle;
    }

    public void setPlantTitle(String plantTitle) {
        this.plantTitle = plantTitle;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getPlantIcon() {
        return plantIcon;
    }

    public void setPlantIcon(String plantIcon) {
        this.plantIcon = plantIcon;
    }

    public PlantData4(String plantTitle, String key, String plantIcon) {
        this.plantTitle = plantTitle;
        this.key = key;
        this.plantIcon = plantIcon;
    }

    public PlantData4(){

    }
}
