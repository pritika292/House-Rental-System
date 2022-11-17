package com.reservation.rentaplace.Domain.Command;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor

public class Coupon
{
    private String couponCode;
    private float couponDiscount;
}
