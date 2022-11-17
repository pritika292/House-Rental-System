package com.reservation.rentaplace.mapper;

import com.reservation.rentaplace.Domain.Cart;
import com.reservation.rentaplace.Domain.Request.CartRequest;
import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CartRowMapper implements RowMapper<CartRow>{
    @Override
    public CartRow mapRow(ResultSet rs, int rowNum) throws SQLException{
        CartRow cartrow = new CartRow();
        cartrow.setCart_id(rs.getInt("cart_id"));
        cartrow.setProperty_ids(rs.getString("property_ids"));
        cartrow.setCheckin_date(rs.getString(("checkin_date")));
        cartrow.setCheckout_date(rs.getString(("checkout_date")));
        cartrow.setCart_value(rs.getFloat("cart_value"));
        return cartrow;
    }
}



