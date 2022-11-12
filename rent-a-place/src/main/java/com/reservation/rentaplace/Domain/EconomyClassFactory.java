package com.reservation.rentaplace.Domain;

public class EconomyClassFactory extends PropertyFactory{
    @Override
    public RentalProperty getProperty(String property_type) {
        return new Motel();
    }
}
