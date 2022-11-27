package com.reservation.rentaplace.Criteria;

import com.reservation.rentaplace.Domain.RentalProperty;
import com.reservation.rentaplace.Domain.SearchPropertyRequest;



import java.util.ArrayList;
import java.util.List;

public class CriteriaPropertyType implements Criteria{
    @Override
    public List<RentalProperty> meetCriteria(List<RentalProperty> rentalPropertyList, SearchPropertyRequest searchPropertyRequest) {

        rentalPropertyList.removeIf(rentalProperty -> Boolean.FALSE.equals(rentalProperty.getProperty_type().equals(searchPropertyRequest.getProperty_type())));

        return rentalPropertyList;
    }
}
