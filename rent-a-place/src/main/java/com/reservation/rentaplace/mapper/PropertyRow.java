package com.reservation.rentaplace.mapper;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PropertyRow {
    private Integer property_id;
    private Float price_per_night;
    private Integer num_of_bedrooms;
    private Integer num_of_bathrooms;
    private String property_description;
    private String property_name;
    private String property_type;
    private String city;
    private Integer pet_friendly;
    private Integer wifi_avail;
    private Integer carpet_area;
    private Float avg_rating;
    private Integer owner_id;
    private Integer availability;
}
