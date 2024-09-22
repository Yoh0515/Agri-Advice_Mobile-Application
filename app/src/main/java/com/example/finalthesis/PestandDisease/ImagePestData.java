package com.example.finalthesis.PestandDisease;

public class ImagePestData {

    private String pestImages;
    private String key;


    public ImagePestData() {
    }

    public ImagePestData(String pestImages, String key) {
        this.pestImages = pestImages;
        this.key = key;
    }

    public String getPestImages() {
        return pestImages;
    }

    public void setPestImages(String pestImages) {
        this.pestImages = pestImages;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}

