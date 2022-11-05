package com.reservation.rentaplace.Domain;

//import com.sun.org.apache.xpath.internal.operations.Bool;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Bean;
import org.springframework.data.relational.core.sql.In;

import java.util.Map;

public class Property
{
    private Integer property_id;
    private Float price_per_night;
    private Integer num_bedrooms;
    private Integer num_baths;
    private String desc;
    private String property_name;
    private String property_type;
    private String city;
    private Integer pet_friendly;
    private Integer wifi_avail;
    private Integer carpet_area;
    private Float average_rating;
    private Integer owner_id;
    private Map<String[], Boolean> availability;

    public Integer getProperty_id() {
        return property_id;
    }

    public void setProperty_id(Integer property_id) {
        this.property_id = property_id;
    }

    public Float getPrice_per_night() {
        return price_per_night;
    }

    public void setPrice_per_night(Float price_per_night) {
        this.price_per_night = price_per_night;
    }

    public Integer getNum_bedrooms() {
        return num_bedrooms;
    }

    public void setNum_bedrooms(Integer num_bedrooms) {
        this.num_bedrooms = num_bedrooms;
    }

    public Integer getNum_baths() {
        return num_baths;
    }

    public void setNum_baths(Integer num_baths) {
        this.num_baths = num_baths;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getProperty_name() {
        return property_name;
    }

    public void setProperty_name(String property_name) {
        this.property_name = property_name;
    }

    public String getProperty_type() {
        return property_type;
    }

    public void setProperty_type(String property_type) {
        this.property_type = property_type;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Integer getPet_friendly() {
        return pet_friendly;
    }

    public void setPet_friendly(Integer pet_friendly) {
        this.pet_friendly = pet_friendly;
    }

    public Integer getWifi_avail() {
        return wifi_avail;
    }

    public void setWifi_avail(Integer wifi_avail) {
        this.wifi_avail = wifi_avail;
    }

    public Integer getCarpet_area() {
        return carpet_area;
    }

    public void setCarpet_area(Integer carpet_area) {
        this.carpet_area = carpet_area;
    }

    public Float getAverage_rating() {
        return average_rating;
    }

    public void setAverage_rating(Float average_rating) {
        this.average_rating = average_rating;
    }

    public Integer getOwner_id() {
        return owner_id;
    }

    public void setOwner_id(Integer owner_id) {
        this.owner_id = owner_id;
    }

    public Map<String[], Boolean> getAvailability() {
        return availability;
    }

    public void setAvailability(Map<String[], Boolean> availability) {
        this.availability = availability;
    }
}
