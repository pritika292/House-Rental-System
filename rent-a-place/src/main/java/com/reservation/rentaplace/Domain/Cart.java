package com.reservation.rentaplace.Domain;

import com.reservation.rentaplace.DAO.DBMgr;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Cart {
    private ArrayList<RentalProperty> property;
    private ArrayList<Date> checkinDate;
    private ArrayList<Date> checkoutDate;
    private int cartID;
    private float cartValue;

    public boolean verifyCart(ArrayList<Reservation> reservations) {
        for(int i=0;i<property.size();i++){
            for(int j=0;j<reservations.size();j++){
                if(property.get(i).getProperty_id() == reservations.get(j).getProperty().getProperty_id()) {
                    if (checkinDate.get(i).compareTo(reservations.get(j).getCheckinDate()) >= 0 && checkinDate.get(i).compareTo(reservations.get(j).getCheckoutDate()) <= 0) {
                        return false;
                    }
                    if (checkoutDate.get(i).compareTo(reservations.get(j).getCheckinDate()) >= 0 && checkoutDate.get(i).compareTo(reservations.get(j).getCheckoutDate()) <= 0) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public void addToCart(RentalProperty p, Date checkInDate, Date checkOutDate) {
        this.property.add(p);
        this.checkinDate.add(checkInDate);
        this.checkoutDate.add(checkOutDate);
        this.cartValue = this.cartValue + p.getPrice_per_night() * getDays(checkInDate, checkOutDate);
    }

    public long getDays(Date checkInDate, Date checkOutDate) {
        long timeDifference = Math.abs(checkInDate.getTime() - checkOutDate.getTime());
        long days = TimeUnit.DAYS.convert(timeDifference, TimeUnit.MILLISECONDS) + 1;
        System.out.println("Number of days booked: " + days);

        return days;
    }
}
