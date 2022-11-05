package com.reservation.rentaplace.mapper;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.reservation.rentaplace.Domain.Customer;
import org.springframework.jdbc.core.RowMapper;

public class CustomerRowMapper implements RowMapper<Customer> {
    @Override
    public Customer mapRow(ResultSet rs, int rowNum) throws SQLException {
        Customer customer = new Customer();
        customer.setCust_id(rs.getInt("customer_id"));
        customer.setCust_name(rs.getString("customer_name"));
        customer.setUsername(rs.getString("username"));
        customer.setPassword(rs.getString("password"));
        customer.setEmail(rs.getString("email"));
        customer.setPhone_num(rs.getString("phone_number"));
        customer.setCart_id(rs.getInt("cart_id"));
        return customer;
    }
}