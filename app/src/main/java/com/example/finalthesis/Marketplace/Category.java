package com.example.finalthesis.Marketplace;

public class Category {

    String mCategory;

    public String getmCategory() {
        return mCategory;
    }

    public void setmCategory(String mCategory) {
        this.mCategory = mCategory;
    }

    public Category(){
    }

    @Override
    public String toString() {
        return mCategory; // return the value you want to display in the dropdown
    }
}
