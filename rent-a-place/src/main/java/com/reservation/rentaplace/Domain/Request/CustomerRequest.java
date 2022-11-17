package com.reservation.rentaplace.Domain.Request;
import lombok.Setter;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.regex.Pattern;

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

    public boolean verifyUsername(){
        if(this.username.length() > 10){
            return false;
        }
        return true;
    }
    public boolean verifyEmail(){
        String regexPattern = "^(.+)@(\\S+)$";
        return Pattern.compile(regexPattern)
                .matcher(this.email)
                .matches();
    }

    public boolean verifyPhoneNumber(){
        String regexPattern = "^(\\d{3}-?){2}\\d{4}$";
        return Pattern.compile(regexPattern)
                .matcher(this.phone_number)
                .matches();
    }
}
