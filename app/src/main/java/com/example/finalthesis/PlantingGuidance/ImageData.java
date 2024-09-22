package com.example.finalthesis.PlantingGuidance;

public class ImageData {

    private String pestDiseaseUrls;
    private String key;


    public ImageData() {
    }

    public ImageData(String pestDiseaseUrls, String key) {
        this.pestDiseaseUrls = pestDiseaseUrls;
        this.key = key;
    }

    public String getPestDiseaseUrls() {
        return pestDiseaseUrls;
    }

    public void setPestDiseaseUrls(String pestDiseaseUrls) {
        this.pestDiseaseUrls = pestDiseaseUrls;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
