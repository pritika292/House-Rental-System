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
        for(int i=0;i<rentalPropertiesList.size();i++){
            for (Reservation reservation : reservations) {
                if (rentalPropertiesList.get(i).getProperty_id() == reservation.getProperty().getProperty_id()) {
                    if (checkinDate.compareTo(reservation.getCheckinDate()) >= 0 && checkinDate.compareTo(reservation.getCheckoutDate()) <= 0) {
                        rentalPropertiesList.remove(i);
                    }
                    if (checkoutDate.compareTo(reservation.getCheckinDate()) >= 0 && checkoutDate.compareTo(reservation.getCheckoutDate()) <= 0) {
                        rentalPropertiesList.remove(i);
                    }
                }
            }
        }
        return rentalPropertiesList;
    }

    public void assignPropertiesList(List<RentalProperty> propertiesList, Date checkIn, Date checkOut) {
        this.rentalPropertiesList.addAll(propertiesList);
        this.checkinDate = checkIn;
        this.checkoutDate = checkOut;
    }

}