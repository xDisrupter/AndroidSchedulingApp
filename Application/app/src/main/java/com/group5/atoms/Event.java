package com.group5.atoms;

public class Event {
    private long calendarID;
    private long id;
    private String date;
    private String email;
    private String event;

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

    public Event(long calendarID, long id, String date, String email, String event) {
        this.calendarID = calendarID;
        this.id = id;
        this.date = date;
        this.email = email;
        this.event = event;
    }

    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append(event+" \n "+date);
        String str = sb.toString();
        return str;
    }
}
