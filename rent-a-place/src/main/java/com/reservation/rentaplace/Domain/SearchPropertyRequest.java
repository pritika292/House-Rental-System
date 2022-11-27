package com.reservation.rentaplace.Domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SearchPropertyRequest {
    private String city;
    private String checkIn;
    private String checkOut;

    private Float price_per_night;
    private Integer num_bedrooms;
    private Integer num_baths;
    private String property_type;
    private Integer pet_friendly;
    private Integer wifi_avail;
    private Integer carpet_area;
    private Float average_rating;
    private Integer availability;

}
