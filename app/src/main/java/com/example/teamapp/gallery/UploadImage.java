package com.example.teamapp.gallery;

public class UploadImage {

    String userName;
    String imageUrl;

    public UploadImage(String userName, String imageUrl) {
        this.userName = userName;
        this.imageUrl = imageUrl;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
