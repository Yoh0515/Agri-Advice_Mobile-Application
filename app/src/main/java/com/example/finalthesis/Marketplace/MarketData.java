package com.example.finalthesis.Marketplace;

import java.util.ArrayList;
import java.util.List;

public class MarketData {

    private String title;
    private String price;
    private String category;
    private String description;
    private String location;
    private List<String> mProduct; // List to hold image URLs
    private String key;
    private String vendor;
    private String intApprove;
    private String unit;
    private String quantity;
    private int totalItemOrders;
    private List<Feedback> feedbackArray; // List to hold feedback

    public MarketData(String title, String price, String category, String description, String location, List<String> mProduct, String vendor, String intApprove, String unit, String quantity, List<Feedback> feedbackArray, int totalItemOrders) {
        this.title = title;
        this.price = price;
        this.category = category;
        this.description = description;
        this.location = location;
        this.mProduct = mProduct;
        this.vendor = vendor;
        this.intApprove = intApprove;
        this.unit = unit;
        this.quantity = quantity;
        this.totalItemOrders = totalItemOrders;
        this.feedbackArray = feedbackArray;
    }

    public MarketData() {
        // Default constructor
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getTotalItemOrders() {
        return totalItemOrders;
    }

    public void setTotalItemOrders(int totalItemOrders) {
        this.totalItemOrders = totalItemOrders;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public List<String> getmProduct() {
        return mProduct;
    }

    public void setmProduct(List<String> mProduct) {
        this.mProduct = mProduct;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public String getIntApprove() {
        return intApprove;
    }

    public void setIntApprove(String intApprove) {
        this.intApprove = intApprove;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public List<Feedback> getFeedbackArray() {
        return feedbackArray;
    }

    public void setFeedbackArray(List<Feedback> feedbackArray) {
        this.feedbackArray = feedbackArray;
    }

    public static class Feedback {
        private String feedbackText;
        private int rating;
        private String reviewerName;
        private String reviewDate;
        private List<String> feedbackImages; // Changed to List<String> for sequential URLs

        public Feedback() {
            // Default constructor required for calls to DataSnapshot.getValue(Feedback.class)
            this.feedbackImages = new ArrayList<>();
        }

        public Feedback(String feedbackText, int rating, String reviewerName, String reviewDate) {
            this.feedbackText = feedbackText;
            this.rating = rating;
            this.reviewerName = reviewerName;
            this.reviewDate = reviewDate;
            this.feedbackImages = feedbackImages != null ? feedbackImages : new ArrayList<>();
        }

        // Getter and Setter methods...

        public String getFeedbackText() {
            return feedbackText;
        }

        public void setFeedbackText(String feedbackText) {
            this.feedbackText = feedbackText;
        }

        public int getRating() {
            return rating;
        }

        public void setRating(int rating) {
            this.rating = rating;
        }

        public String getReviewerName() {
            return reviewerName;
        }

        public void setReviewerName(String reviewerName) {
            this.reviewerName = reviewerName;
        }

        public String getReviewDate() {
            return reviewDate;
        }

        public void setReviewDate(String reviewDate) {
            this.reviewDate = reviewDate;
        }

        public List<String> getFeedbackImages() {
            return feedbackImages;
        }

        public void setFeedbackImages(List<String> feedbackImages) {
            this.feedbackImages = feedbackImages;
        }
    }



}
