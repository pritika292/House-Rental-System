package com.reservation.rentaplace.Domain;

import lombok.Getter;

public class Customer
{

    private String username;
    private String password;
    @Getter
    Cart cart;

    public boolean verify(String username, String password)
    {
        if (username.equals(this.username) && password.equals(this.password))
        {
            return true;
        }
        return false;
    }

}
