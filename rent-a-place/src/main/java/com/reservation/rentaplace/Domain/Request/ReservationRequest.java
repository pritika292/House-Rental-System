package com.reservation.rentaplace.Domain.Request;

import com.reservation.rentaplace.Domain.Command.Coupon;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReservationRequest {
    //Integer cartID
    String username;
    private List<Coupon> coupons;
}
