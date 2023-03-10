package com.reservation.rentaplace.Domain.Request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RatePropertyRequest {
    private  Integer reservationID;
    private Integer propertyID;
    private float rating;
}
