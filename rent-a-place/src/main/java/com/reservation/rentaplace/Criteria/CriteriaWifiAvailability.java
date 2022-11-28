package com.reservation.rentaplace.Criteria;

import com.reservation.rentaplace.Domain.RentalProperty;
import com.reservation.rentaplace.Domain.SearchPropertyRequest;

import java.util.List;

public class CriteriaWifiAvailability implements Criteria{
    @Override
    public List<RentalProperty> meetCriteria(List<RentalProperty> rentalPropertyList, SearchPropertyRequest searchPropertyRequest) {

        rentalPropertyList.removeIf(rentalProperty -> Boolean.valueOf(rentalProperty.getWifi_avail().equals(searchPropertyRequest.getWifi_avail())).equals(Boolean.FALSE));

        return rentalPropertyList;
    }
}