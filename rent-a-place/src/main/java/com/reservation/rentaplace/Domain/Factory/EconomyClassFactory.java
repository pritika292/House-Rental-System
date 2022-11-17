package com.reservation.rentaplace.Domain.Factory;

import com.reservation.rentaplace.Domain.Motel;
import com.reservation.rentaplace.Domain.RentalProperty;

public class EconomyClassFactory extends PropertyFactory {
    @Override
    public RentalProperty getProperty(String property_type) {
        return new Motel();
    }
}
