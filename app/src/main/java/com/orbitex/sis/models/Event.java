package com.orbitex.sis.models;

public class Event {

    private String id;
    private String title;
    private String organizer;
    private String dateTime;
    private String location;
    private String coverImageUrl;
    private boolean isPaid;
    private String price;

    public Event() {} // Firestore required

    // Getters & setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public String getOrganizer() { return organizer; }
    public String getDateTime() { return dateTime; }
    public String getLocation() { return location; }
    public String getCoverImageUrl() { return coverImageUrl; }
    public boolean isPaid() { return isPaid; }
    public String getPrice() { return price; }
}

