package com.reservation.rentaplace.mapper;

import com.reservation.rentaplace.Domain.Cart;
import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CartRowMapper implements RowMapper<Cart>{
    @Override
    public Cart mapRow(ResultSet rs, int rowNum) throws SQLException{
        Cart cart = new Cart();
        cart.setCartID(rs.getInt("cart_id"));
        cart.setProperty(rs.getString("property_ids"));
        cart.setCheckinDate(rs.getString("checkin_date"));
        cart.setCheckoutDate(rs.getString("checkout_date"));
        return cart;
    }
}



