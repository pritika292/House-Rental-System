package com.reservation.rentaplace.Controller;
import com.nimbusds.jose.shaded.json.JSONObject;
import com.reservation.rentaplace.Domain.*;
import com.reservation.rentaplace.DAO.DBMgr;
import com.reservation.rentaplace.Domain.Command.CouponList;
import com.reservation.rentaplace.Domain.Command.InvoiceGenerator;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.reservation.rentaplace.Domain.Constants;

import javax.swing.text.html.parser.Entity;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
public class Controller
{
    @Autowired
    private DBMgr db;
    @Autowired
    private CustomerService service;

    public Controller()
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
    @PostMapping("/generateInvoice/{uname}")
    public float generateInvoice(@RequestBody(required = false) CouponList c, @PathVariable String uname)
    {

        List<Float> couponDiscounts = null;
        if (c.getCoupons()!= null)
        {
            couponDiscounts = new ArrayList<>();
            for (int i = 0; i < c.getCoupons().size(); i++)
            {
                couponDiscounts.add(c.getCoupons().get(i).getCouponDiscount());
            }
        }
        Cart customerCart = getCustomerCart(uname);
        if (customerCart.getCartValue() == 0)
        {
            throw new ResourceNotFoundException("User does not have any properties in cart");
        }
        else
        {
            InvoiceGenerator generator = InvoiceGenerator.getInvoiceGenerator();
            return generator.generateInvoice(couponDiscounts, customerCart.getCartValue());
        }

    }
    //Private helper method for generateInvoice
    private Cart getCustomerCart(String uname)
    {
        Customer customer = db.getCustomer(uname);
        if (customer == null)
        {
            throw new ResourceNotFoundException("User not found");
        }
        Cart customerCart = db.getCart(customer.getUserID());
        return customerCart;
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

    @PostMapping("/cart/remove/")
    public String removeFromCart(@RequestBody CartRequest c){
        // validate user
        Customer user = db.getCustomer(c.getUsername());
        Cart cart = user.getCart();
        if(user == null){
            throw new InvalidRequestException("Invalid user");
        }
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
        int idx = getDeleteCartIndex(cart,c, sdf);
        if(idx==-1){
            throw new ResourceNotFoundException("Property does not exist in cart");
        }
        long days = cart.getDays(cart.getCheckinDate().get(idx), cart.getCheckoutDate().get(idx));
        float updatedCartValue = cart.getCartValue() - cart.getProperty().get(idx).getPrice_per_night()*days;
        cart.getProperty().remove(idx);
        cart.getCheckinDate().remove(idx);
        cart.getCheckoutDate().remove(idx);

        cart.setCartValue(updatedCartValue);
        user.setCart(cart);
        int updateResult = db.updateCart(user);
        if(updateResult == 1)
            return "Removed from cart successfully";
        else
            throw new RuntimeException("Error occurred, cannot remove from cart");
    }

    private int getDeleteCartIndex(Cart cart, CartRequest c, SimpleDateFormat sdf){
        ArrayList<RentalProperty> properties = cart.getProperty();
        ArrayList<Date> inDates = cart.getCheckinDate();
        ArrayList<Date> outDates = cart.getCheckoutDate();
        int index = -1;
        Date inDate = null;
        Date outDate = null;
        try{
            inDate = sdf.parse(c.getCheckinDate());
            outDate = sdf.parse(c.getCheckoutDate());
        }
        catch(Exception e){
            System.out.println(e);
        }
        for(int i=0;i<properties.size();i++){
            if(properties.get(i).getProperty_id() == c.getPropertyID()){
                if(inDates.get(i).compareTo(inDate) == 0 && outDates.get(i).compareTo(outDate)==0){
                    index =i;
                    break;
                }
            }
        }
        return index;
    }
    @GetMapping(path = "/cart/view/{username}", produces= MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> viewCart(@PathVariable String username) {
        Customer user = db.getCustomer(username);
        Cart cart = user.getCart();
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
        List<JSONObject> entities = new ArrayList<JSONObject>();
        for (int i=0;i<cart.getProperty().size();i++) {
            JSONObject entity = new JSONObject();
            entity.put("Property", cart.getProperty().get(i));
            String inDate = sdf.format(cart.getCheckinDate().get(i));
            entity.put("Checkin date", inDate);
            String outDate = sdf.format(cart.getCheckoutDate().get(i));
            entity.put("Checkout date", outDate);
            entities.add(entity);
        }
        JSONObject price = new JSONObject();
        price.put("Cart value", cart.getCartValue());
        entities.add(price);

        return new ResponseEntity<Object>(entities, HttpStatus.OK);
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
    @PostMapping("/reserve/{username}")
    public String create(@PathVariable String username) {
        Customer c = db.getCustomer(username);
        Cart cart = c.getCart();
        ArrayList<Reservation> r = db.getReservations();
        if(cart.verifyCart(r)){
            return "Verified cart";
        }
        return "Invalid cart";
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
