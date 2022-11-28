package com.reservation.rentaplace.Criteria;

import com.reservation.rentaplace.Domain.RentalProperty;
import com.reservation.rentaplace.Domain.SearchPropertyRequest;

import java.util.List;

public class CriteriaAverageRating implements Criteria {
    @Override
    public List<RentalProperty> meetCriteria(List<RentalProperty> rentalPropertyList, SearchPropertyRequest searchPropertyRequest) {

        rentalPropertyList.removeIf(rentalProperty ->  Boolean.valueOf((searchPropertyRequest.getAverage_rating()) <= rentalProperty.getAverage_rating()).equals(Boolean.FALSE));

        return rentalPropertyList;
    }
}