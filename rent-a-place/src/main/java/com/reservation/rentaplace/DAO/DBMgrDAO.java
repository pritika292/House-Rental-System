package com.reservation.rentaplace.DAO;

import com.reservation.rentaplace.Domain.Customer;
import com.reservation.rentaplace.Domain.CustomerRequest;
import com.reservation.rentaplace.Domain.Property;
import com.reservation.rentaplace.Domain.Reservation;

public interface DBMgrDAO {
    public Customer getCustomer(String uname);

    public Property getProperty(String location, String[] date);

    public Property getProperty(String property_id);

    public Reservation getReservation(String uname, String property_id);

    public int save(Reservation r);

    public int save(CustomerRequest c, int cartId);

    public int save(Property p);

    public int create(Property p, Customer u);
}
