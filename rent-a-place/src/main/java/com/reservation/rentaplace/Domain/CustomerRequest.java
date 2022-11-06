package com.reservation.rentaplace.Domain;
import lombok.Setter;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CustomerRequest {
    private String name;
    private String username;
    private String password;
    private String email;
    private String phone_number;

}
