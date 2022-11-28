package com.reservation.rentaplace.Criteria;

import com.reservation.rentaplace.Domain.RentalProperty;
import com.reservation.rentaplace.Domain.SearchPropertyRequest;
import org.apache.commons.lang3.BooleanUtils;

import java.util.ArrayList;
import java.util.List;

public class CriteriaAverageRating implements Criteria {
    @Override
    public List<RentalProperty> meetCriteria(List<RentalProperty> rentalPropertyList, SearchPropertyRequest searchPropertyRequest) {
        List<RentalProperty> AverageRatingFilterList = new ArrayList<RentalProperty>();

        rentalPropertyList.removeIf(rentalProperty ->  Boolean.valueOf((searchPropertyRequest.getAverage_rating()) <= rentalProperty.getAverage_rating()).equals(Boolean.FALSE));

        return rentalPropertyList;
    }
}