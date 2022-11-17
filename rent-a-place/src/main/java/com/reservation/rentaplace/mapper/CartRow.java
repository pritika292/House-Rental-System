package com.reservation.rentaplace.mapper;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartRow {
    Integer cart_id;
    String property_ids;
    String checkout_date;
    String checkin_date;
    Float cart_value;
}
