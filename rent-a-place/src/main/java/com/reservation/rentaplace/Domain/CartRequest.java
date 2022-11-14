package com.reservation.rentaplace.Domain;
import lombok.Setter;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.Date;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CartRequest {
    String propertyID;
    String checkinDate;
    String checkoutDate;
    String username;
}
