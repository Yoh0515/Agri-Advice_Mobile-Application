package com.example.finalthesis.PestandDisease;

public class Data_Disease {


    private String plantTitle;
    private String key;
    private String diseaseDescription;
    private String iconUrl;
    private String pdfUrl;
    private String biologicalControl;
    private String culturalControl;
    private String chemicalControl;
    private String diseaseDevelopment;
    private String symptoms;

    public Data_Disease() {
    }

    public Data_Disease(String plantTitle, String key, String diseaseDescription, String iconUrl, String pdfUrl, String biologicalControl, String culturalControl, String chemicalControl, String diseaseDevelopment, String symptoms) {
        this.plantTitle = plantTitle;
        this.key = key;
        this.diseaseDescription = diseaseDescription;
        this.iconUrl = iconUrl;
        this.pdfUrl = pdfUrl;
        this.biologicalControl = biologicalControl;
        this.culturalControl = culturalControl;
        this.chemicalControl = chemicalControl;
        this.diseaseDevelopment = diseaseDevelopment;
        this.symptoms = symptoms;
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

    public String getDiseaseDescription() {
        return diseaseDescription;
    }

    public void setDiseaseDescription(String diseaseDescription) {
        this.diseaseDescription = diseaseDescription;
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

    public String getBiologicalControl() {
        return biologicalControl;
    }

    public void setBiologicalControl(String biologicalControl) {
        this.biologicalControl = biologicalControl;
    }

    public String getCulturalControl() {
        return culturalControl;
    }

    public void setCulturalControl(String culturalControl) {
        this.culturalControl = culturalControl;
    }

    public String getChemicalControl() {
        return chemicalControl;
    }

    public void setChemicalControl(String chemicalControl) {
        this.chemicalControl = chemicalControl;
    }

    public String getDiseaseDevelopment() {
        return diseaseDevelopment;
    }

    public void setDiseaseDevelopment(String diseaseDevelopment) {
        this.diseaseDevelopment = diseaseDevelopment;
    }

    public String getSymptoms() {
        return symptoms;
    }

    public void setSymptoms(String symptoms) {
        this.symptoms = symptoms;
    }
}
