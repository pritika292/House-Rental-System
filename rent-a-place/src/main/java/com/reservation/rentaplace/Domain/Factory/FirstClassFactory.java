package com.reservation.rentaplace.Domain.Factory;

import com.reservation.rentaplace.Domain.*;

public class FirstClassFactory extends PropertyFactory {

    @Override
    public RentalProperty getProperty(String property_type) {
        if(property_type.equalsIgnoreCase("Villa")){
            return new Villa();
        }
        else if(property_type.equalsIgnoreCase("BeachHouse")){
            return new BeachHouse();
        }
        else if(property_type.equalsIgnoreCase("Resort")){
            return new Resort();
        }
        else return null;
    }
}
