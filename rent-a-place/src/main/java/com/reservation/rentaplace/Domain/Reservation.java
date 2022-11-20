package com.reservation.rentaplace.Domain;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class Reservation
{
    private Customer customer;
    private int confirmationNumber;
    private RentalProperty Property;
    private Date checkinDate;
    private Date checkoutDate;
}
