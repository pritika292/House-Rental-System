package com.reservation.rentaplace.Domain;

import lombok.Getter;

public class Cart
{
    @Getter
    Property[] property;

    public boolean verifyCart(String[] date)
    {
        return false;
    }
}
