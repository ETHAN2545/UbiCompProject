package com.example.dublin8community.models;

public class Event {

    public String eventId;
    public String eventName;
    public String description;
    public String category;

    public String locationName;
    public String address;

    public double latitude;
    public double longitude;

    public long startTime;
    public long endTime;

    public String imageUrl;
    public String createdBy;
    public long createdAt;

    public Event() {

    }

    public Event(String eventId, String eventName, String description, String category, String locationName, String address, double latitude, double longitude, long startTime, long endTime, String imageUrl, String createdBy, long createdAt) {

        this.eventId = eventId;
        this.eventName = eventName;
        this.description = description;
        this.category = category;

        this.locationName = locationName;
        this.address = address;

        this.latitude = latitude;
        this.longitude = longitude;

        this.startTime = startTime;
        this.endTime = endTime;

        this.imageUrl = imageUrl;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
    }
}
