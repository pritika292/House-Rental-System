package com.reservation.rentaplace.Domain;

import org.springframework.context.annotation.Bean;

public class Property
{

    private String description;
    private String location;
    private boolean availability;
    private double rating;
    private double carpet_area;
    private int number_of_rooms;
    private int number_of_baths;
    private boolean wifi_facility;
    private boolean swimming_pool;
    private boolean pet_friendly;
    private String property_name;
    private float price;

    public String getProperty_name() {
        return property_name;
    }

    public void setProperty_name(String property_name) {
        this.property_name = property_name;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public boolean isAvailability() {
        return availability;
    }

    public void setAvailability(boolean availability) {
        this.availability = availability;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public double getCarpet_area() {
        return carpet_area;
    }

    public void setCarpet_area(double carpet_area) {
        this.carpet_area = carpet_area;
    }

    public int getNumber_of_rooms() {
        return number_of_rooms;
    }

    public void setNumber_of_rooms(int number_of_rooms) {
        this.number_of_rooms = number_of_rooms;
    }

    public int getNumber_of_baths() {
        return number_of_baths;
    }

    public void setNumber_of_baths(int number_of_baths) {
        this.number_of_baths = number_of_baths;
    }

    public boolean isWifi_facility() {
        return wifi_facility;
    }

    public void setWifi_facility(boolean wifi_facility) {
        this.wifi_facility = wifi_facility;
    }

    public boolean isSwimming_pool() {
        return swimming_pool;
    }

    public void setSwimming_pool(boolean swimming_pool) {
        this.swimming_pool = swimming_pool;
    }

    public boolean isPet_friendly() {
        return pet_friendly;
    }

    public void setPet_friendly(boolean pet_friendly) {
        this.pet_friendly = pet_friendly;
    }


}
