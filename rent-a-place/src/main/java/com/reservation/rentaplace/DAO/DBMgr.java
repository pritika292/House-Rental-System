package com.reservation.rentaplace.DAO;
import com.reservation.rentaplace.Domain.*;
import com.reservation.rentaplace.mapper.CustomerRowMapper;
import com.reservation.rentaplace.mapper.CartRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@Repository
public class DBMgr implements DBMgrDAO
{
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public Customer getCustomer(String uname)
    {
        String query = "select * from CUSTOMER WHERE username = ?";
        try {
            Customer c = jdbcTemplate.queryForObject(query, new CustomerRowMapper(), uname);
            System.out.println("User not null");
            Cart cart = getCart(c.getUserID());
            c.setCart(cart);
            return c;
        }
        catch (Exception e) {
            System.out.println("User is null");
            return null;
        }
    }

    @Override
    public RentalProperty getProperty(String location, String[] date) {
        return null;
    }

    @Override
    public RentalProperty getProperty(String propertyID, String propertyType) {
        return null;
    }
    public String checkProperty(String property_id){
        String query = "select property_type from Property WHERE property_id = ?";
        try{
            String property_type = (String)jdbcTemplate.queryForObject(query, String.class, property_id);
            return property_type;
        }
        catch (Exception e){
            System.out.println(e);
            return null;
        }
    }
    @Override
    public Reservation getReservation(String uname, String property_id)
    {
        return null;
    }

    @Override
    public int save(Reservation r) {
        return 0;
    }
    public int createCart(){
        String insert_sql = "INSERT INTO Cart (property_ids) VALUES (?)";
        try{
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(
                    new PreparedStatementCreator() {
                        public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                            PreparedStatement ps =
                                    connection.prepareStatement(insert_sql, new String[] {"id"});
                            ps.setString(1, "");
                            return ps;
                        }
                    },
                    keyHolder);
            return keyHolder.getKey().intValue();
        }
        catch (Exception e){
            System.out.println(e);
            return -1;
        }
    }
    public Cart getCart(int user_id){
        try{
            Cart cart = jdbcTemplate.queryForObject("SELECT * from Cart where cart_id in (SELECT cart_id from Customer where customer_id = (?))", new CartRowMapper(), user_id);
            return cart;
        }
        catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }
    public int updateCart(Customer c){
        String update_sql = "";
        try{
            Cart cart = c.getCart();
            jdbcTemplate.update("UPDATE Cart SET property_ids = (?), checkin_date = (?), checkout_date = (?) WHERE cart_id = (?)", new Object[] {cart.getProperty(), cart.getCheckinDate(), cart.getCheckoutDate(), cart.getCartID()});
            return 1;
        }
        catch(Exception e){
            System.out.println(e);
            return -1;
        }
    }
    @Override
    public int save(CustomerRequest c, int cartId){
        try{
            jdbcTemplate.update("INSERT INTO Customer (customer_name, username, password, email, phone_number, cart_id) VALUES (?, ?, ?, ?, ?, ?)", new Object[] {c.getName(), c.getUsername(), c.getPassword(), c.getEmail(), c.getPhone_number(), cartId});
            return 1;
        }
        catch (Exception e) {
            System.out.println(e);
            return 0;
        }
    }
    @Override
    public int save(RentalProperty p) {
        try {
            return jdbcTemplate.update("INSERT INTO PROPERTY (price_per_night, num_of_bedrooms, num_of_bathrooms," +
                            " property_description, property_name, property_type, city, pet_friendly, wifi_avail, carpet_area," +
                            " avg_rating, owner_id, availability) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                    new Object[]{p.getPrice_per_night(), p.getNum_bedrooms(), p.getNum_baths(), p.getProperty_description(), 
                            p.getProperty_name(), p.getProperty_type(), p.getCity(), p.getPet_friendly(), p.getWifi_avail(),
                            p.getCarpet_area(), p.getAverage_rating(), p.getOwner_id(), p.getAvailability()});
        }
        catch (Exception e){
            return 0;
        }
    }
    
    @Override
    public int create(Property p, Customer u){
        return 0;
    }
}
