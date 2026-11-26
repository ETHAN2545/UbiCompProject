package com.example.dublin8community.models;

public class Service {

    public String serviceId;
    public String serviceName;
    public String description;
    public String category;

    public String address;

    public double latitude;
    public double longitude;

    public String openingHours;
    public String contactEmail;
    public String contactPhone;

    public String createdBy;
    public long createdAt;

    public Service() {
    }

    public Service(String serviceId, String serviceName, String description,
                   String category, String address,
                   double latitude, double longitude,
                   String openingHours, String contactEmail,
                   String contactPhone, String createdBy, long createdAt) {

        this.serviceId = serviceId;
        this.serviceName = serviceName;
        this.description = description;
        this.category = category;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.openingHours = openingHours;
        this.contactEmail = contactEmail;
        this.contactPhone = contactPhone;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
    }
}
