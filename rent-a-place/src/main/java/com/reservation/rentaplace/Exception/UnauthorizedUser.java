package com.reservation.rentaplace.Exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.UNAUTHORIZED)
public class UnauthorizedUser extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public UnauthorizedUser(String message) {
        super(message);
    }
}





