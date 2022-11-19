package com.reservation.rentaplace.mapper;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReservationRow {
    private int reservation_id;
    private int customer_id;
    private int property_id;
    private String checkin_date;
    private String checkout_date;
}
