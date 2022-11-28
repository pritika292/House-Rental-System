package com.reservation.rentaplace.Criteria;

import com.reservation.rentaplace.Domain.RentalProperty;
import com.reservation.rentaplace.Domain.SearchPropertyRequest;

import java.util.List;

public class CriteriaNumberOfBathrooms implements Criteria{
    @Override
    public List<RentalProperty> meetCriteria(List<RentalProperty> rentalPropertyList, SearchPropertyRequest searchPropertyRequest) {

        rentalPropertyList.removeIf(rentalProperty -> Boolean.valueOf(rentalProperty.getNum_baths().equals(searchPropertyRequest.getNum_baths())).equals(Boolean.FALSE));

        return rentalPropertyList;
    }
}