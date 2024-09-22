package com.example.finalthesis.PestandDisease;

public class Data_Pest {

    private String plantTitle;
    private String key;
    private String description;
    private String iconUrl;
    private String pdfUrl;
    private String controlMeasures;
    private String damageSymptoms;
    private String insectCharacteristics;
    private String scientificName;

    public Data_Pest() {
    }

    public Data_Pest(String plantTitle, String key, String description, String iconUrl, String pdfUrl, String controlMeasures, String damageSymptoms, String insectCharacteristics, String scientificName) {
        this.plantTitle = plantTitle;
        this.key = key;
        this.description = description;
        this.iconUrl = iconUrl;
        this.pdfUrl = pdfUrl;
        this.controlMeasures = controlMeasures;
        this.damageSymptoms = damageSymptoms;
        this.insectCharacteristics = insectCharacteristics;
        this.scientificName = scientificName;
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

    public String getControlMeasures() {
        return controlMeasures;
    }

    public void setControlMeasures(String controlMeasures) {
        this.controlMeasures = controlMeasures;
    }

    public String getDamageSymptoms() {
        return damageSymptoms;
    }

    public void setDamageSymptoms(String damageSymptoms) {
        this.damageSymptoms = damageSymptoms;
    }

    public String getInsectCharacteristics() {
        return insectCharacteristics;
    }

    public void setInsectCharacteristics(String insectCharacteristics) {
        this.insectCharacteristics = insectCharacteristics;
    }

    public String getScientificName() {
        return scientificName;
    }

    public void setScientificName(String scientificName) {
        this.scientificName = scientificName;
    }
}
