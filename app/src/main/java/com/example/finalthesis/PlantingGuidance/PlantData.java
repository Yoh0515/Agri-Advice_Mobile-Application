package com.example.finalthesis.PlantingGuidance;

public class PlantData {

    private String title;

    private String plantTitle;
    private String key;
    private String description;
    private String iconUrl;
    private String pdfUrl;
    private String category;
    private String depth;
    private String water;
    private String sun;
    private String temperature;
    private String grow;
    private String planting;
    private String feed;
    private String harvest;
    private String storage;
    private String rangeValue;

    public PlantData() {
        // Default constructor required for calls to DataSnapshot.getValue(PlantData.class)
    }

    public PlantData(String plantTitle, String description, String title, String key, String iconUrl, String pdfUrl, String category) {
        this.description = description;
        this.title = title;
        this.key = key;
        this.iconUrl = iconUrl;
        this.pdfUrl = pdfUrl;
        this.category = category;
        this.plantTitle = plantTitle;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public String getPdfUrl() {
        return pdfUrl;
    }

    public void setPdfUrl(String pdfUrl) {
        this.pdfUrl = pdfUrl;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDepth() {
        return depth;
    }

    public void setDepth(String depth) {
        this.depth = depth;
    }

    public String getWater() {
        return water;
    }

    public void setWater(String water) {
        this.water = water;
    }

    public String getSun() {
        return sun;
    }

    public void setSun(String sun) {
        this.sun = sun;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getGrow() {
        return grow;
    }

    public void setGrow(String grow) {
        this.grow = grow;
    }

    public String getPlanting() {
        return planting;
    }

    public void setPlanting(String planting) {
        this.planting = planting;
    }

    public String getFeed() {
        return feed;
    }

    public void setFeed(String feed) {
        this.feed = feed;
    }

    public String getHarvest() {
        return harvest;
    }

    public void setHarvest(String harvest) {
        this.harvest = harvest;
    }

    public String getStorage() {
        return storage;
    }

    public void setStorage(String storage) {
        this.storage = storage;
    }

    public String getRangeValue() {
        return rangeValue;
    }

    public void setRangeValue(String rangeValue) {
        this.rangeValue = rangeValue;
    }
}
