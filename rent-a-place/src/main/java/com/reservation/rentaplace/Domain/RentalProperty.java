package com.reservation.rentaplace.Domain;
//import com.sun.org.apache.xpath.internal.operations.Bool;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public abstract class RentalProperty implements Property
{
    private Integer property_id;
    private Float price_per_night;
    private Integer num_bedrooms;
    private Integer num_baths;
    private String property_description;
    private String property_name;
    private String property_type;
    private String city;
    private Integer pet_friendly;
    private Integer wifi_avail;
    private Integer carpet_area;
    private Float average_rating;
    private Integer owner_id;
    private Integer availability;

    public float getPrice() {
        return price_per_night;
    }
}
