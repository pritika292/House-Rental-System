package com.reservation.rentaplace.Controller;
import com.reservation.rentaplace.Domain.*;
import com.reservation.rentaplace.DAO.DBMgr;
import com.reservation.rentaplace.Domain.Factory.FactoryProducer;
import com.reservation.rentaplace.Domain.Factory.PropertyFactory;
import com.reservation.rentaplace.Domain.Request.CartRequest;
import com.reservation.rentaplace.Domain.Request.CustomerRequest;
import com.reservation.rentaplace.Domain.Request.HostPropertyRequest;
import com.reservation.rentaplace.Domain.Validator.DateValidator;
import com.reservation.rentaplace.Domain.Validator.DateValidatorUsingDateFormat;
import com.reservation.rentaplace.Domain.Property;
import com.reservation.rentaplace.Domain.Login;
import com.reservation.rentaplace.Domain.Filter;
import com.reservation.rentaplace.Exception.InvalidRequestException;
import com.reservation.rentaplace.Exception.ResourceNotFoundException;
import com.reservation.rentaplace.Service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.reservation.rentaplace.Domain.Constants;

import java.text.SimpleDateFormat;
import java.util.Date;

@RestController
public class Controller
{
    @Autowired
    private DBMgr db;
    @Autowired
    private CustomerService service;

    public Controller(DBMgr db)
    {
        this.db = db;
    }
    @PostMapping("/register")
    public String save(@RequestBody CustomerRequest c) {
        if(!c.verifyUsername())
            throw new InvalidRequestException("Username cannot exceed the length of 10");
        if(!c.verifyEmail())
            throw new InvalidRequestException("Invalid email id");
        if(!c.verifyPhoneNumber())
            throw new InvalidRequestException("Invalid phone number");

        int cartId = db.createCart();
        if(cartId == -1)
            throw new RuntimeException("Error occurred");
        if(db.save(c, cartId) == 0){
            throw new RuntimeException("Error occurred");
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
                else
                    throw new InvalidRequestException("Login unsuccessful - invalid password");
            }
            else{
                throw new ResourceNotFoundException("Login unsuccessful - invalid username");
            }
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
        int propertyID = c.getPropertyID();
        validateProperty(propertyID);
        // Validate dates
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
        String result = validateInputDates(c.getCheckinDate(), c.getCheckoutDate(), sdf);
        if(result != null){
            throw new InvalidRequestException(result);
        }
        //Add to cart
        Cart cart = user.getCart();
        RentalProperty p = db.getProperty(propertyID);
        try{
            cart.addToCart(p,sdf.parse(c.getCheckinDate()), sdf.parse(c.getCheckoutDate()));
        }
        catch(Exception e){
            System.out.println(e);
            throw new RuntimeException("Error occurred, cannot add to cart");
        }
        user.setCart(cart);
        int updateResult = db.updateCart(user);
        if(updateResult == 1)
            return "Added to cart successfully";
        else
            throw new RuntimeException("Error occurred, cannot add to cart");
    }

    public void validateProperty(int propertyID){
        String property_type = db.checkProperty(propertyID);
        if(property_type == null){
            throw new InvalidRequestException("Invalid property id : "+ propertyID);
        }
    }
    private String validateInputDates(String checkinDate, String checkoutDate, SimpleDateFormat sdf){
        DateValidator validator = new DateValidatorUsingDateFormat("MM-dd-yyyy");
        if(!validator.isValid(checkinDate)){
            return "Invalid check-in date";
        }
        if(!validator.isValid(checkoutDate)){
            return "Invalid check-out date";
        }
        try{
            Date currentDate = new Date();
            Date inDate = sdf.parse(checkinDate);
            Date outDate= sdf.parse(checkoutDate);

            //reservation dates can be from today or in the future
            if(inDate.compareTo(currentDate) < 0){
                return "Please select a future checkin date.";
            }
            if(outDate.compareTo(currentDate) < 0){
                return "Please select a future checkout date.";
            }
            //checkout date should be after checkin date
            if(checkoutDate.compareTo(checkinDate) < 0){
                return "Checkin date is after the checkout date, please select valid dates";
            }
        }
        catch(Exception e){
            System.out.println(e);
        }
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
        if(Constants.getPropertyClass().containsKey(propertyType)){
            FactoryProducer producer = FactoryProducer.getInstance();
            PropertyFactory factory = producer.getFactory(Constants.getPropertyClass().get(propertyType));
            RentalProperty property = factory.getProperty(propertyType);
            property = setProperty(property, hp);

            if(db.save(property) == 1)
                return "Hosted property successfully.";
            throw new RuntimeException("Could not host property.");
        }
        else
            throw new InvalidRequestException("Property type should belong to (Villa, BeachHouse, Resort, Apartment, Studio, House, Motel).");
    }

    private RentalProperty setProperty(RentalProperty property, HostPropertyRequest hp){
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

        return property;
    }

}
