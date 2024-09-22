package com.example.finalthesis.Marketplace;

import java.util.List;

public class MessageModel {
    private String sender;
    private String senderName;
    private String receiver;
    private String receiverName;
    private String message;
    private long timestamp;
    private List<String> images;
    private List<String> pdfs; // New field for PDF URLs

    public MessageModel() {
        // Required empty constructor for Firestore serialization
    }

    public MessageModel(String sender, String receiver, String message, String senderName, String receiverName, long timestamp, List<String> images, List<String> pdfs) {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.senderName = senderName;
        this.receiverName = receiverName;
        this.timestamp = timestamp;
        this.images = images;
        this.pdfs = pdfs;
    }

    // Getters and setters
    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public List<String> getPdfs() {
        return pdfs;
    }

    public void setPdfs(List<String> pdfs) {
        this.pdfs = pdfs;
    }
}
