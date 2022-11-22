package com.reservation.rentaplace.Domain.Command;

public class AddTax implements AddOn
{
    private static final float TAX_VALUE_PERCENTAGE = 0.1f;


    @Override
    public float executeTransaction(float amount) {
        amount+=(amount * TAX_VALUE_PERCENTAGE);
        return amount;
    }


}