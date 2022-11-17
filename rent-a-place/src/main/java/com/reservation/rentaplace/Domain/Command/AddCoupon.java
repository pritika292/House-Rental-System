package com.reservation.rentaplace.Domain.Command;

public class AddCoupon implements AddOn
{
    private float coupon;

    public AddCoupon(float coupon)
    {
        this.coupon = coupon;
    }
    @Override
    public float executeTransaction(float amount) {
        amount -= (amount * coupon);
        return amount;
    }
}
