package com.example.finalthesis.PlantingGuidance;

public class ImageItem {
    private String images;
    private String key;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
    }

    public ImageItem(String images) {
        this.images = images;
    }
}

