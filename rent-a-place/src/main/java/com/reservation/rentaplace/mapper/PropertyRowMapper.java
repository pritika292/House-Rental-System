package com.reservation.rentaplace.mapper;

import com.reservation.rentaplace.Domain.Property;
import com.reservation.rentaplace.Domain.RentalProperty;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PropertyRowMapper implements RowMapper<PropertyRow> {
    @Override
    public PropertyRow mapRow(ResultSet rs, int rowNum) throws SQLException {
        PropertyRow property = new PropertyRow();
        property.setProperty_id(rs.getInt("property_id"));
        property.setProperty_name(rs.getString("property_name"));
        property.setProperty_type(rs.getString("property_type"));
        property.setCity(rs.getString("city"));
        property.setProperty_description(rs.getString("property_description"));
        property.setAvg_rating(rs.getFloat("avg_rating"));
        property.setPrice_per_night(rs.getFloat("price_per_night"));
        property.setNum_of_bathrooms((rs.getInt("num_of_bathrooms")));
        property.setNum_of_bedrooms(rs.getInt("num_of_bedrooms"));
        property.setCarpet_area(rs.getInt("carpet_area"));
        property.setPet_friendly(rs.getInt("pet_friendly"));
        property.setWifi_avail(rs.getInt("wifi_avail"));
        property.setOwner_id(rs.getInt("owner_id"));
        property.setAvailability(rs.getInt("availability"));
        return property;
    }
}