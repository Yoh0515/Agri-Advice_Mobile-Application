package com.example.finalthesis;
public class User {
    public String username;
    public String fullname;
    public String email;
    public String address;
    public String phoneNumber;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String username, String fullname, String email, String address, String phoneNumber) {
        this.username = username;
        this.fullname = fullname;
        this.email = email;
        this.address = address;
        this.phoneNumber = phoneNumber;
    }
}
