package com.example.teamapp;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Team {
    private String dateStr,name,description,teamImage,teamId,lastEdit;
    private boolean album,chat,bill;
    private Date last;
    private Date date;


    public Team(String date,String name, String description,String teamImage,String teamId,boolean album,
                boolean chat, boolean bill) {
        this.dateStr = date;
        this.name = name;
        this.description = description;
        this.teamImage = teamImage;
        this.teamId = teamId;
        this.album = album;
        this.chat = chat;
        this.bill = bill;
    }

    public Team(String date,String name, String description,String teamImage,String teamId,boolean album,
                boolean chat, boolean bill,String lastEdit) throws ParseException {
        this.dateStr = date;
        this.name = name;
        this.description = description;
        this.teamImage = teamImage;
        this.teamId = teamId;
        this.album = album;
        this.chat = chat;
        this.bill = bill;
        this.lastEdit = lastEdit;
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        this.last = formatter.parse(lastEdit);
        SimpleDateFormat formatter1 = new SimpleDateFormat("dd/MM/yyyy");
        this.date = formatter1.parse(date);
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Date getLast() {
        return last;
    }

    public void setLast(Date last) {
        this.last = last;
    }

    public boolean isChat() {
        return chat;
    }

    public void setChat(boolean chat) {
        this.chat = chat;
    }

    public boolean isBill() {
        return bill;
    }

    public void setBill(boolean bill) {
        this.bill = bill;
    }

    public boolean isAlbum() {
        return album;
    }

    public void setAlbum(boolean album) {
        this.album = album;
    }

    public Team(String name, String teamImage, String teamId) {
        this.name = name;
        this.teamImage = teamImage;
        this.teamId = teamId;
    }

    public String getLastEdit() {
        return lastEdit;
    }

    public void setLastEdit(String lastEdit) {
        this.lastEdit = lastEdit;
    }

    public String getTeamId() {
        return teamId;
    }

    public void setTeamId(String teamId) {
        this.teamId = teamId;
    }

    public String getTeamImage() {
        return teamImage;
    }

    public void setTeamImage(String teamImage) {
        this.teamImage = teamImage;
    }

    public String getDateStr() {
        return dateStr;
    }

    public void setDateStr(String dateStr) {
        this.dateStr = dateStr;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
