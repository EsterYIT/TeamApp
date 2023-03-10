package com.example.teamapp.GroupChat;

import androidx.annotation.NonNull;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class GroupMessage {
    String message;
    String name;
    String key;
    String senderId,readableDateTime;
    Date dateTime;

    public GroupMessage(){}

    public GroupMessage(String message, String name,String senderId,Date dateTime) {
        this.message = message;
        this.name = name;
        this.senderId=senderId;
        this.dateTime=dateTime;
        this.readableDateTime=getReadableDateTime(dateTime);
    }

    public Date getDateTime() {
        return dateTime;
    }

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
        this.readableDateTime=getReadableDateTime(dateTime);
    }


    public String getSenderId() {
        return senderId;
    }

    public String getReadableDateTime() {
        return readableDateTime;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    private String getReadableDateTime(Date date){
        return new SimpleDateFormat("MMMM dd, yyyy - hh:mm a", Locale.getDefault()).format(date);
    }

    @NonNull
    @Override
    public String toString() {
        return "Message{"+"message='"+message+"\'"
                +", name='"+name+"\'"
                +", key='"+key+"\'"+"}";
    }
}
