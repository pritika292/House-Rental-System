package com.reservation.rentaplace.DAO;

import com.reservation.rentaplace.Domain.*;
import com.reservation.rentaplace.Domain.Request.CustomerRequest;

import java.util.ArrayList;
import java.util.List;

public interface DBMgrDAO {
    public Customer getCustomer(String uname);

    public Customer getCustomerByID(int uid);

    public int endSession(Customer c);

    public List<RentalProperty> getProperties(SearchPropertyRequest searchPropertyRequest);

    public Property getProperty(Integer propertyID);

    public String checkProperty(Integer propertyID);

    public ArrayList<Reservation> getReservations();

    public int createCart();

    public Cart getCart(int userID);

    public int updateCart(Customer c);

    public int save(CustomerRequest c, int cartId);

    public int createSession(Customer c, String key);

    public int hostProperty(RentalProperty p);

    public int makeReservation(ArrayList<Reservation> reservations);

    public Integer saveRating(Integer propertyID, double newRating, Integer numberOfReviews);
    }
