package com.reservation.rentaplace.DAO;
import com.reservation.rentaplace.Domain.CustomerRequest;
import com.reservation.rentaplace.mapper.CustomerRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import com.reservation.rentaplace.Domain.Customer;
import com.reservation.rentaplace.Domain.Property;
import com.reservation.rentaplace.Domain.Reservation;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
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
        Customer c =  null;
        try {
            c = jdbcTemplate.queryForObject(query, new CustomerRowMapper(), uname);
            System.out.println("not null");
            return c;
        }
        catch (Exception e) {
            System.out.println("is null");
            return null;
        }
    }

    @Override
    public Property getProperty(String location, String[] date)
    {
        return null;
    }

    @Override
    public Property getProperty(String property_id)
    {
        return null;
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
    public int save(Property p) {
        return 0;
    }
    @Override
    public int create(Property p, Customer u){
        return 0;
    }
}
