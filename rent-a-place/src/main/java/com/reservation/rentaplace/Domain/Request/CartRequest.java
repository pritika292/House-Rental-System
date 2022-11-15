package com.reservation.rentaplace.Domain.Request;
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
    Integer propertyID;
    String checkinDate;
    String checkoutDate;
    String username;
}
