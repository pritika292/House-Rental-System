package com.reservation.rentaplace.DAO;

import com.reservation.rentaplace.Domain.*;

public interface DBMgrDAO {
    public Customer getCustomer(String uname);

    public Property getProperty(String location, String[] date);

    public Property getProperty(String propertyID, String propertyType);

    public Reservation getReservation(String uname, String property_id);

    public int save(Reservation r);

    public int save(CustomerRequest c, int cartId);

    int save(RentalProperty p);

    public int create(Property p, Customer u);
}
