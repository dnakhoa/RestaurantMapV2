package com.example.restaurantmap.Model;

public class Restaurant {
    // Instance variables
    private int id;
    private String name;
    private double lat, lng;

    // Constructors
    public Restaurant(int id, String name, double latitude, double longitude) {
        this.id = id;
        this.name = name;
        this.lat = latitude;
        this.lng = longitude;
    }

    public Restaurant(String name, double latitude, double longitude) {
        this.name = name;
        this.lat = latitude;
        this.lng = longitude;
    }

    // Getters
    public String getName() {
        return this.name;
    }

    public double getLat() {
        return this.lat;
    }

    public double getLng() {
        return this.lng;
    }
}
