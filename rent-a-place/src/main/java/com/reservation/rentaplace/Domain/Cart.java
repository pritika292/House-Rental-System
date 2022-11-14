package com.reservation.rentaplace.Domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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
    private String property;
    private String checkinDate;
    private String checkoutDate;
    private int cartID;

    public boolean verifyCart(String[] date)
    {
        return false;
    }
}
