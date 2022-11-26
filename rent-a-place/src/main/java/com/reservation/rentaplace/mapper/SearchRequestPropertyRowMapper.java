package com.reservation.rentaplace.mapper;

import com.reservation.rentaplace.DAO.DBMgr;
import com.reservation.rentaplace.Domain.Factory.FactoryProducer;
import com.reservation.rentaplace.Domain.Factory.PropertyFactory;
import com.reservation.rentaplace.Domain.RentalProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SearchRequestPropertyRowMapper implements RowMapper<RentalProperty> {

    @Autowired
    private DBMgr db;

    private FactoryProducer producer;

    private final HashMap<String, String> getPropertyClass = new HashMap<>();

    public SearchRequestPropertyRowMapper()
    {
        //this.producer = producer;
        this.db = db;
        getPropertyClass.put("villa", "FirstClass");
        getPropertyClass.put("beachHouse", "FirstClass");
        getPropertyClass.put("resort", "FirstClass");
        getPropertyClass.put("apartment", "BusinessClass");
        getPropertyClass.put("house", "BusinessClass");
        getPropertyClass.put("studio", "BusinessClass");
        getPropertyClass.put("motel", "EconomyClass");
    }

    @Override
    public RentalProperty mapRow(ResultSet rs, int rowNum) throws SQLException {

        List<RentalProperty> properties = new ArrayList<>();

        RentalProperty p = null;

        String propertyType = rs.getString("property_type");
        // Validation
        if (getPropertyClass.containsKey(propertyType)) {
            producer = new FactoryProducer();
            PropertyFactory factory = producer.getFactory(getPropertyClass.get(propertyType));
            RentalProperty property = factory.getProperty(propertyType);

            property.setProperty_id(rs.getInt("property_id"));
            property.setProperty_name(rs.getString("property_name"));
            property.setProperty_type(rs.getString("property_type"));
            property.setCity(rs.getString("city"));
            property.setProperty_description(rs.getString("property_description"));
            property.setAverage_rating(rs.getFloat("avg_rating"));
            property.setPrice_per_night(rs.getFloat("price_per_night"));
            property.setNum_baths(rs.getInt("num_of_bathrooms"));
            property.setNum_bedrooms(rs.getInt("num_of_bedrooms"));
            property.setCarpet_area(rs.getInt("carpet_area"));
            property.setPet_friendly(rs.getInt("pet_friendly"));
            property.setWifi_avail(rs.getInt("wifi_avail"));
            property.setOwner_id(rs.getInt("owner_id"));
            property.setAvailability(rs.getInt("availability"));
            return property;
        }
        return null;
    }
}