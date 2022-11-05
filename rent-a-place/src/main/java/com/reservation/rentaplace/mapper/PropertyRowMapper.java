package com.reservation.rentaplace.mapper;

import com.reservation.rentaplace.Domain.Property;
import com.reservation.rentaplace.Domain.Property;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PropertyRowMapper implements RowMapper<Property> {
    @Override
    public Property mapRow(ResultSet rs, int rowNum) throws SQLException {
        Property property = new Property();
        property.setProperty_id(rs.getInt("property_id"));
        property.setProperty_name(rs.getString("property_name"));
        property.setProperty_type(rs.getString("property_type"));
        property.setCity(rs.getString("city"));
        property.setDesc(rs.getString("property_description"));
        property.setAverage_rating(rs.getFloat("average_rating"));
        property.setPrice_per_night(rs.getFloat("price_per_night"));
        property.setNum_baths(rs.getInt("number_of_baths"));
        property.setNum_bedrooms(rs.getInt("number_of_bedrooms"));
        property.setCarpet_area(rs.getInt("carpet_area"));
        property.setPet_friendly(rs.getInt("pet_friendly"));
        property.setWifi_avail(rs.getInt("wifi_avail"));
        property.setOwner_id(rs.getInt("owner_id"));
//        set availability
        return property;
    }
}