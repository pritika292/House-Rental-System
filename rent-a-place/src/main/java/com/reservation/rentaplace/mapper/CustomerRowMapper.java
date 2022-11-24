package com.reservation.rentaplace.mapper;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.reservation.rentaplace.Domain.Customer;
import org.springframework.jdbc.core.RowMapper;

public class CustomerRowMapper implements RowMapper<Customer> {
    @Override
    public Customer mapRow(ResultSet rs, int rowNum) throws SQLException {
        Customer customer = new Customer();
        customer.setName(rs.getString("customer_name"));
        customer.setEmail(rs.getString("email"));
        customer.setPhone_number(rs.getString("phone_number"));
        customer.setUsername(rs.getString("username"));
        customer.setPassword(rs.getString("password"));
        customer.setUserID(rs.getInt("customer_id"));
        customer.setApiKey(rs.getString("apiKey"));
        return customer;
    }
}