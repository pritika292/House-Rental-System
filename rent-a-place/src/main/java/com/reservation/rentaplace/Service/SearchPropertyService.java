package com.reservation.rentaplace.Service;

import com.reservation.rentaplace.Domain.RentalProperty;
import com.reservation.rentaplace.Domain.Reservation;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Service
public class SearchPropertyService {
    private ArrayList<RentalProperty> rentalPropertiesList = new ArrayList<>();
    private Date checkinDate;
    private Date checkoutDate;

    public List<RentalProperty> verifyProperties(List<Reservation> reservations) {
        List<RentalProperty> rentalPropertyRemovalList = new ArrayList<>();
        for(RentalProperty rentalProperty:rentalPropertiesList){
            for (Reservation reservation : reservations) {
                if (rentalProperty.getProperty_id() == reservation.getProperty().getProperty_id()) {
                    if (checkinDate.compareTo(reservation.getCheckinDate()) >= 0 && checkinDate.compareTo(reservation.getCheckoutDate()) <= 0) {
                        rentalPropertyRemovalList.add(rentalProperty);
                    }
                    else if (checkoutDate.compareTo(reservation.getCheckinDate()) >= 0 && checkoutDate.compareTo(reservation.getCheckoutDate()) <= 0) {
                        rentalPropertyRemovalList.add(rentalProperty);
                    }
                }
            }
        }
        rentalPropertiesList.removeAll(rentalPropertyRemovalList);
        return rentalPropertiesList;
    }

    public void assignPropertiesList(List<RentalProperty> propertiesList, Date checkIn, Date checkOut) {
        this.rentalPropertiesList.addAll(propertiesList);
        this.checkinDate = checkIn;
        this.checkoutDate = checkOut;
    }

}