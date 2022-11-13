package com.reservation.rentaplace.Domain;

public abstract class PropertyFactory {
    public abstract RentalProperty getProperty(String property_type);
}
