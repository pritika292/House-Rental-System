package com.reservation.rentaplace.Controller;
import com.nimbusds.oauth2.sdk.ErrorResponse;
import com.reservation.rentaplace.Domain.*;
import com.reservation.rentaplace.DAO.DBMgr;
import com.reservation.rentaplace.Domain.Validator.DateValidator;
import com.reservation.rentaplace.Domain.Validator.DateValidatorUsingDateFormat;
import com.reservation.rentaplace.Domain.Property;
import com.reservation.rentaplace.Domain.Login;
import com.reservation.rentaplace.Domain.Filter;
import com.reservation.rentaplace.Exception.InvalidRequestException;
import com.reservation.rentaplace.Exception.ResourceNotFoundException;
import com.reservation.rentaplace.Service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.sound.midi.SysexMessage;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

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
    @PostMapping("/cart/add/")
    public String addToCart(@RequestBody CartRequest c) {
        // Validate user
        Customer user = db.getCustomer(c.getUsername());
        if(user == null){
            throw new InvalidRequestException("Invalid user");
        }
        // Validate property id
        String property_id = c.getPropertyID();
        if(db.checkProperty(property_id) == null){
            throw new InvalidRequestException("Invalid property id : "+ property_id);
        }
        // Validate dates
        DateValidator validator = new DateValidatorUsingDateFormat("MM-dd-yyyy");
        if(!validator.isValid(c.getCheckinDate())){
            throw new InvalidRequestException("Invalid check-in date");
        }
        if(!validator.isValid(c.getCheckoutDate())){
            throw new InvalidRequestException("Invalid check-out date");
        }
        //Add to cart
        Cart cart = user.getCart();
        String properties = cart.getProperty();
        String checkin_dates = cart.getCheckinDate();
        String checkout_dates =  cart.getCheckoutDate();
        System.out.println(properties);
        if(properties.isEmpty()){
            properties = c.getPropertyID();
        }
        else{
            properties = properties + "," + c.getPropertyID();
        }
        if(checkin_dates != null){
            checkin_dates = checkin_dates + "," + c.getCheckinDate();
        }
        else{
            checkin_dates = c.getCheckinDate();
        }
        if(checkout_dates != null){
            checkout_dates = checkout_dates + "," + c.getCheckoutDate();
        }
        else{
            checkout_dates = c.getCheckoutDate();
        }
        cart.setProperty(properties);
        cart.setCheckinDate(checkin_dates);
        cart.setCheckoutDate(checkout_dates);
        user.setCart(cart);

        int result = db.updateCart(user);
        if(result==1)
            return "Added to cart successfully";
        else
            throw new RuntimeException("Error occurred, cannot add to cart");

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
