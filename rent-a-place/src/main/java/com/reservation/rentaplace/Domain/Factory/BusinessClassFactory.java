package com.reservation.rentaplace.Domain.Factory;

import com.reservation.rentaplace.Domain.*;

public class BusinessClassFactory extends PropertyFactory {
    @Override
    public RentalProperty getProperty(String property_type) {
        if(property_type.equalsIgnoreCase("Apartment")){
            return new Apartment();
        }
        else if(property_type.equalsIgnoreCase("House")){
            return new House();
        }
        else{
            return new Studio();
        }
    }

}
