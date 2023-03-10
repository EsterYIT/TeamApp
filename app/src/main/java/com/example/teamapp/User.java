package com.example.teamapp;

import java.io.Serializable;

public class User implements Serializable {

    private String username;
    private String email;
    private String phoneNumber;
    private String userImage;
    private String id;
    private String token;
    private String userBill;

    public User(){
    }

    public User(String userImage,String username){
        this.userImage = userImage;
        this.username = username;
    }

    public User(String userImage, String username,String phoneNumber){
        this.userImage = userImage;
        this.username = username;
        this.phoneNumber = phoneNumber;
    }

    public User(String userImage, String username,String id,String userBill,String phone){
        this.userImage = userImage;
        this.username = username;
        this.id = id;
        this.userBill = userBill;
        this.phoneNumber = phone;
    }

    public User(String username, String email, String phoneNumber, String userImage) {
        this.username = username;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.userImage= userImage;
    }

    public String getUserBill() {
        return userBill;
    }

    public void setUserBill(String userBill) {
        this.userBill = userBill;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }




}
