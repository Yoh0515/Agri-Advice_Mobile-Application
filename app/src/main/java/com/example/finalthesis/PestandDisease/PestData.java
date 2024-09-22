package com.example.finalthesis.PestandDisease;

public class PestData {
    private String title;
    private String iconUrl;
    private String pdfUrl;
    private String plantTitle;
    private String key;
    private String chemical;
    private String damage;
    private String imageUrl;
    private String identify;
    private String physical;

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public String getPlantTitle() {
        return plantTitle;
    }

    public void setPlantTitle(String plantTitle) {
        this.plantTitle = plantTitle;
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

    public String getChemical() {
        return chemical;
    }

    public void setChemical(String chemical) {
        this.chemical = chemical;
    }

    public String getDamage() {
        return damage;
    }

    public void setDamage(String damage) {
        this.damage = damage;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getIdentify() {
        return identify;
    }

    public void setIdentify(String identify) {
        this.identify = identify;
    }

    public String getPhysical() {
        return physical;
    }

    public void setPhysical(String physical) {
        this.physical = physical;
    }

    public String getPdfUrl() {
        return pdfUrl;
    }

    public void setPdfUrl(String pdfUrl) {
        this.pdfUrl = pdfUrl;
    }

    public PestData(String title, String iconUrl, String plantTitle, String key, String chemical, String damage, String imageUrl, String pdfUrl, String identify, String physical) {
        this.title = title;
        this.iconUrl = iconUrl;
        this.plantTitle = plantTitle;
        this.key = key;
        this.chemical = chemical;
        this.damage = damage;
        this.imageUrl = imageUrl;
        this.pdfUrl = pdfUrl;
        this.identify = identify;
        this.physical = physical;
    }

    public PestData(){

    }
}
