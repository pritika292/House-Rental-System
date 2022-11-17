package com.reservation.rentaplace.Domain.Factory;

import com.reservation.rentaplace.Domain.RentalProperty;

public abstract class PropertyFactory {
    public abstract RentalProperty getProperty(String property_type);
}
