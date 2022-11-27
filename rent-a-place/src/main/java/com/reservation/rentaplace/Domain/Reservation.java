package com.reservation.rentaplace.Domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Reservation
{
    private Customer customer;
    private int confirmationNumber;
    private RentalProperty Property;
    private Date checkinDate;
    private Date checkoutDate;
    private float invoiceAmount;
}
