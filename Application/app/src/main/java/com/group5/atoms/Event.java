package com.group5.atoms;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

//Event class to store event data
public class Event{

    //class members
    private long calendarID;
    private long id;
    private String date;
    private String email;
    private String event;
    private String startTime;
    private String endTime;

    public long getCalendarID() {
        return calendarID;
    }

    public void setCalendarID(long calendarID) {
        this.calendarID = calendarID;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public Event(long calendarID, long id, String date, String email, String event, String startTime, String endTime) {
        this.calendarID = calendarID;
        this.id = id;
        this.date = date;
        this.email = email;
        this.event = event;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append(event+" \n "+date);
        String str = sb.toString();
        return str;
    }
}
