package com.reservation.rentaplace.Domain;
import lombok.Setter;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Customer
{
    private String name;
    private String username;
    private String password;
    
    private String email;

    private String phone_number;

    private Cart cart;
    public boolean verify(String username, String password)
    {
        if (username.equals(this.username) && password.equals(this.password))
            return true;
        return false;
    }

}
