package com.reservation.rentaplace.Domain;

import lombok.Getter;

public class Reservation
{
    @Getter
    private Cart cart;
    @Getter
    private Customer customer;
}
