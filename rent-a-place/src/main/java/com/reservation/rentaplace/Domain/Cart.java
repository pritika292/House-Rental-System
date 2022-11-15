package com.reservation.rentaplace.Domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Cart
{
    private ArrayList<RentalProperty> property;
    private ArrayList<Date> checkinDate;
    private ArrayList<Date> checkoutDate;
    private int cartID;
    private float cartValue;

    public boolean verifyCart(String[] date)
    {
        return false;
    }
    public void addToCart(RentalProperty p, Date checkInDate, Date checkOutDate){
        this.property.add(p);
        this.checkinDate.add(checkInDate);
        this.checkoutDate.add(checkOutDate);
        this.cartValue = this.cartValue + p.getPrice_per_night();
    }
}
