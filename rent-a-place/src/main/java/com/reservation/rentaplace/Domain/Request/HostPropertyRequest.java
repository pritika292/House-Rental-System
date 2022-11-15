package com.reservation.rentaplace.Domain.Request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class HostPropertyRequest {
    private float price_per_night;
    private Integer num_of_bedrooms;
    private Integer num_of_bathrooms;
    private String property_description;
    private String property_name;
    private String property_type;
    private String city;
    private Integer pet_friendly;
    private Integer wifi_avail;
    private Integer carpet_area;
    private float avg_rating;
    private Integer owner_id;
    private Integer availability;

}
