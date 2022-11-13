package com.reservation.rentaplace.Controller;
import com.reservation.rentaplace.Domain.*;
import com.reservation.rentaplace.DAO.DBMgr;

import com.reservation.rentaplace.Domain.Property;
import com.reservation.rentaplace.Domain.Login;
import com.reservation.rentaplace.Domain.Filter;
import com.reservation.rentaplace.Service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.sound.midi.SysexMessage;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;

@RestController
public class Controller
{
    @Autowired
    private DBMgr db;
    @Autowired
    private CustomerService service;
    private FactoryProducer producer;
    private final HashMap<String, String> getPropertyClass = new HashMap<>();

    public Controller(DBMgr db)
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
    @PostMapping("/register")
    public String save(@RequestBody CustomerRequest c) {
        if(!c.verifyUsername())
            return "Username cannot exceed the length of 10";
        if(!c.verifyEmail())
            return "Invalid email id";
        if(!c.verifyPhoneNumber())
            return "Invalid phone number";

        int cartId = db.createCart();
        if(cartId == -1)
            return "Error occurred";
        if(db.save(c, cartId) == 0){
            return "Error occurred";
        }
        return c.getName()+ " registered successfully";
    }
    @PostMapping("/login")
    public String login(@RequestBody Login l)
    {
            Customer c = db.getCustomer(l.getUsername());
            if (c != null) {
                if (c.verifyPassword(l.getPassword())) {
                    return "Login successful";
                }
            }
            return "Login unsuccessful";
            //Do not delete below code
//        if (l.getUsername() != null && l.getPassword() != null)
//        {
//            return service.verifyLogin(l.getUsername(), l.getPassword());
//        }
//        return "Login unsuccessful";

    }
    @GetMapping("/view/{location}/{dates}")
    public RentalProperty getProperty(@PathVariable String location , @PathVariable String[] dates) {
        return null;
    }
    @GetMapping("/search/")
    public String search(@RequestBody Filter f) {
        return null;
    }
    @PostMapping("/reserve")
    public String create(Property p, Customer u) {
        return null;
    }
    @PostMapping("/rate/{confirmationNumber}/{rating}")
    public static void rate_property(@PathVariable String confirmationNumber , @PathVariable Float rating) {

    }
    @PostMapping("/hostProperty")
    public String hostProperty(@RequestBody HostPropertyRequest hp){
        // this would be Villa, Resort, BeachHouse, Apartment, Studio, Motel
        String propertyType = hp.getProperty_type().toLowerCase();

        // Validation
        if(getPropertyClass.containsKey(propertyType)){
            producer = new FactoryProducer();
            PropertyFactory factory = producer.getFactory(getPropertyClass.get(propertyType));
            RentalProperty property = factory.getProperty(propertyType);

            property.setPrice_per_night(hp.getPrice_per_night());
            property.setNum_bedrooms(hp.getNum_of_bedrooms());
            property.setNum_baths(hp.getNum_of_bathrooms());
            property.setProperty_description(hp.getProperty_description());
            property.setProperty_name(hp.getProperty_name());
            property.setProperty_type(hp.getProperty_type());
            property.setCity(hp.getCity());
            property.setPet_friendly(hp.getPet_friendly());
            property.setWifi_avail(hp.getWifi_avail());
            property.setCarpet_area(hp.getCarpet_area());
            property.setAverage_rating(hp.getAvg_rating());
            property.setOwner_id(hp.getOwner_id());
            property.setAvailability(hp.getAvailability());

            if(db.save(property) == 1)
                return "Hosted Property sucessfully.";
            return "Hosted Property not saved successfully.";
        }
        else
            return "Property type should belong to (Villa, BeachHouse, Resort, Apartment, Studio, House, Motel).";
    }

}
