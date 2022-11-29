package com.reservation.rentaplace.mapper;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ReservationRowMapper implements RowMapper<ReservationRow> {
    @Override
    public ReservationRow mapRow(ResultSet rs, int rowNum) throws SQLException {
        ReservationRow reservation = new ReservationRow();
        reservation.setReservation_id(rs.getInt("reservation_id"));
        reservation.setCustomer_id(rs.getInt("customer_id"));
        reservation.setProperty_id(rs.getInt("property_id"));
        reservation.setCheckin_date(rs.getString("checkin_date"));
        reservation.setCheckout_date(rs.getString("checkout_date"));
        reservation.setInvoice_amount(rs.getFloat("invoice_amount"));
        return reservation;
    }
}
