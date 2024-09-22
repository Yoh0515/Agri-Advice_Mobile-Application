package com.example.finalthesis.Marketplace;

public class MarketCategoryData {

    private String categ;
    private String key;


    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getCateg() {
        return categ;
    }

    public void setCateg(String categ) {
        this.categ = categ;
    }

    public MarketCategoryData(String categ, String key) {
        this.categ = categ;
        this.key = key;
    }

    public MarketCategoryData(){

    }
}
