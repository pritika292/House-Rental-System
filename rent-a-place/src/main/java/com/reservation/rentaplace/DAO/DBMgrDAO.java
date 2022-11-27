package com.reservation.rentaplace.DAO;

import com.reservation.rentaplace.Domain.*;
import com.reservation.rentaplace.Domain.Request.CustomerRequest;

import java.util.List;

public interface DBMgrDAO {
    public Customer getCustomer(String uname);

    public Customer getCustomerByID(int uid);

    public List<RentalProperty> getProperties(SearchPropertyRequest searchPropertyRequest);

    public Property getProperty(Integer propertyID);

    public Reservation getReservation(String uname, String property_id);

    public int save(Reservation r);

    public int save(CustomerRequest c, int cartId);

    public int createSession(Customer c, String key);

    int save(RentalProperty p);

    public int create(Property p, Customer u);
}
