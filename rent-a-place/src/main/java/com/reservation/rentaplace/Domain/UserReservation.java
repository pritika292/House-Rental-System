package com.reservation.rentaplace.Domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserReservation
{
    private Customer customer;
    private int confirmationNumber;
    private List<RentalProperty> PropertyIds;
    private List<Date> checkinDate;
    private List<Date> checkoutDate;
    private float invoice_amount;

}
