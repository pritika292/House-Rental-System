package com.reservation.rentaplace.Domain;

import lombok.Getter;

public class Cart
{
    @Getter
    Property[] property;
    public int cart_id;

    public boolean verifyCart(String[] date)
    {

        return false;
    }
}
