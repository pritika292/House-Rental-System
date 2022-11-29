package com.reservation.rentaplace.Domain.Command;

public class AddProcessingFee implements AddOn
{
    private static final float processingFee = 0.02f;

    @Override
    public float executeTransaction(float amount)
    {
        amount+=amount*processingFee;
        return amount;
    }
}
