package com.example.myapplication;

import java.util.Date;

public class Activity {
    private String user;
    private String typeActivity;
    private String date;

    public Activity(String user, String typeActivity, String date) {
        this.user = user;
        this.typeActivity = typeActivity;
        this.date = date;
    }
    public Activity(String user, String typeActivity) {
        this.user = user;
        this.typeActivity = typeActivity;
        this.date = new Date().toString();
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getTypeActivity() {
        return typeActivity;
    }
    public void setTypeActivity(String typeActivity) {
        this.typeActivity = typeActivity;
    }
    public String getDate() {
        return date;
    }
    public void setDate(){
        this.date = date;
    }
}
