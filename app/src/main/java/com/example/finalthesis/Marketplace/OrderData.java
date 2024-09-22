package com.example.finalthesis.Marketplace;

public class OrderData {

    private String imageUrl;
    private String paymentDate;
    private String referenceNumber;
    private String totalPrice;
    private String transactionId;
    private String userName;
    private String vendor;
    private String image;
    private String isConfirm;
    private String key;


    public OrderData() {
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(String paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getReferenceNumber() {
        return referenceNumber;
    }

    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }

    public String getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(String totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getIsConfirm() {
        return isConfirm;
    }

    public void setIsConfirm(String isConfirm) {
        this.isConfirm = isConfirm;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public OrderData(String imageUrl, String paymentDate, String referenceNumber, String totalPrice, String transactionId, String userName, String vendor, String image, String isConfirm, String key) {
        this.imageUrl = imageUrl;
        this.paymentDate = paymentDate;
        this.referenceNumber = referenceNumber;
        this.totalPrice = totalPrice;
        this.transactionId = transactionId;
        this.userName = userName;
        this.vendor = vendor;
        this.image = image;
        this.isConfirm = isConfirm;
        this.key = key;
    }
}
