package com.example.finalthesis.Menu;

public class SeminarData {

    private String key;
    private String message;
    private String requestStatus;
    private String pdfUrl;
    private long timestamp;


    public SeminarData() {
    }

    public SeminarData(String key, String message, String requestStatus, String pdfUrl, long timestamp) {
        this.key = key;
        this.message = message;
        this.requestStatus = requestStatus;
        this.pdfUrl = pdfUrl;
        this.timestamp = timestamp;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getRequestStatus() {
        return requestStatus;
    }

    public void setRequestStatus(String requestStatus) {
        this.requestStatus = requestStatus;
    }

    public String getPdfUrl() {
        return pdfUrl;
    }

    public void setPdfUrl(String pdfUrl) {
        this.pdfUrl = pdfUrl;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
