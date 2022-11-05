package com.reservation.rentaplace.Domain;
import lombok.Setter;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Filter {
    private int price_per_night;
    private int property_type;
    private int city;
    private int num_of_bedrooms;
    private int num_of_bathrooms;
    private int wifi_avail;
    private int avg_rating;
}
