package com.reservation.rentaplace.Criteria;

import com.reservation.rentaplace.Domain.RentalProperty;
import com.reservation.rentaplace.Domain.SearchPropertyRequest;

import java.util.List;

public class CriteriaCarpetArea implements Criteria{
    @Override
    public List<RentalProperty> meetCriteria(List<RentalProperty> rentalPropertyList, SearchPropertyRequest searchPropertyRequest) {

        rentalPropertyList.removeIf(rentalProperty -> Boolean.valueOf(rentalProperty.getCarpet_area() >= (searchPropertyRequest.getCarpet_area())).equals(Boolean.FALSE));


        return rentalPropertyList;
    }
}