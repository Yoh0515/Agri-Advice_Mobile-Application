package com.example.finalthesis.Marketplace;

public class Vendor {
    private String idNumber;
    private String id;
    private String vendorName;
    private String vendor;
    private String idImageUrl;

    public Vendor() {
    }

    public Vendor(String idNumber, String id, String vendorName, String vendor, String idImageUrl) {
        this.idNumber = idNumber;
        this.id = id;
        this.vendorName = vendorName;
        this.vendor = vendor;
        this.idImageUrl = idImageUrl;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVendorName() {
        return vendorName;
    }

    public void setVendorName(String vendorName) {
        this.vendorName = vendorName;
    }

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public String getIdImageUrl() {
        return idImageUrl;
    }

    public void setIdImageUrl(String idImageUrl) {
        this.idImageUrl = idImageUrl;
    }
}

