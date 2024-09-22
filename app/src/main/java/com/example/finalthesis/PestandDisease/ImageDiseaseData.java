package com.example.finalthesis.PestandDisease;

public class ImageDiseaseData {

    private String diseaseImages;
    private String key;


    public ImageDiseaseData() {
    }

    public ImageDiseaseData(String diseaseImages, String key) {
        this.diseaseImages = diseaseImages;
        this.key = key;
    }

    public String getdiseaseImages() {
        return diseaseImages;
    }

    public void setdiseaseImages(String diseaseImages) {
        this.diseaseImages = diseaseImages;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}

