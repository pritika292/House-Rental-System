package com.reservation.rentaplace.Criteria;

import com.reservation.rentaplace.Domain.RentalProperty;
import com.reservation.rentaplace.Domain.SearchPropertyRequest;


import java.util.List;

public class CriteriaPricePerNight implements Criteria{
    @Override
    public List<RentalProperty> meetCriteria(List<RentalProperty> rentalPropertyList, SearchPropertyRequest searchPropertyRequest) {

        rentalPropertyList.removeIf(rentalProperty -> Boolean.valueOf(rentalProperty.getPrice_per_night() <= (searchPropertyRequest.getPrice_per_night())).equals(Boolean.FALSE));

        return rentalPropertyList;
    }
}
