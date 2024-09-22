package com.example.finalthesis.Menu;

public class ReportData {

    private String isResolved;
    private String key;
    private String senderName;
    private String message;
    private String subject;
    private long timestamp;

    public ReportData() {
    }

    public ReportData(String isResolved, String key, String senderName, String message, String subject, long timestamp) {
        this.isResolved = isResolved;
        this.key = key;
        this.senderName = senderName;
        this.message = message;
        this.subject = subject;
        this.timestamp = timestamp;
    }

    public String getIsResolved() {
        return isResolved;
    }

    public void setIsResolved(String isResolved) {
        this.isResolved = isResolved;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
