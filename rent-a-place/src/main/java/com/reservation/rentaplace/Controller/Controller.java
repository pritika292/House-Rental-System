package com.reservation.rentaplace.Controller;
import com.nimbusds.jose.shaded.json.JSONObject;
import com.reservation.rentaplace.Criteria.*;
import com.reservation.rentaplace.Domain.*;
import com.reservation.rentaplace.DAO.DBMgr;
import com.reservation.rentaplace.Domain.Command.Coupon;
import com.reservation.rentaplace.Domain.Command.CouponList;
import com.reservation.rentaplace.Domain.Command.InvoiceGenerator;
import com.reservation.rentaplace.Domain.Factory.FactoryProducer;
import com.reservation.rentaplace.Domain.Factory.PropertyFactory;
import com.reservation.rentaplace.Domain.Request.CartRequest;
import com.reservation.rentaplace.Domain.Request.CustomerRequest;
import com.reservation.rentaplace.Domain.Request.HostPropertyRequest;
import com.reservation.rentaplace.Domain.Request.ReservationRequest;
import com.reservation.rentaplace.Domain.Validator.DateValidator;
import com.reservation.rentaplace.Domain.Validator.DateValidatorUsingDateFormat;
import com.reservation.rentaplace.Domain.Login;
import com.reservation.rentaplace.Exception.InvalidRequestException;
import com.reservation.rentaplace.Exception.ResourceNotFoundException;
import com.reservation.rentaplace.Exception.UnauthorizedException;
import com.reservation.rentaplace.Service.CustomerService;
import com.reservation.rentaplace.Service.SearchPropertyService;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.reservation.rentaplace.Domain.Constants;
import com.reservation.rentaplace.Domain.Reservation;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Getter
@Setter
@RestController
public class Controller
{
    @Autowired
    private DBMgr db = DBMgr.getInstance();

    @Autowired
    private CustomerService service;

    @Autowired
    private SearchPropertyService searchPropertyService;

    @PostMapping("/register")
    public String register(@RequestBody CustomerRequest c) {
        if(!c.verifyUsername())
            throw new InvalidRequestException("Username cannot exceed the length of 10");
        if(!c.verifyEmail())
            throw new InvalidRequestException("Invalid email id");
        if(!c.verifyPhoneNumber())
            throw new InvalidRequestException("Invalid phone number");
        if(c.getPassword() == "")
            throw new InvalidRequestException("Password cannot empty. Please enter valid password.");

        int cartId = db.createCart();
        if(cartId == -1)
            throw new RuntimeException("Error occurred");
        if(db.save(c, cartId) == 0){
            throw new RuntimeException("Error occurred");
        }
        return c.getName()+ " registered successfully.";
    }
    @PostMapping("/login")
    public String login(@RequestBody Login l)
    {
            Customer c = db.getCustomer(l.getUsername());
            if (c != null) {
                if (c.verifyPassword(l.getPassword())) {
                    String key = db.generateMD5Hashvalue(c.getUsername());
                    c.setApiKey(key);
                    if(db.createSession(c,key) == 1)
                        return "Login successful. API Key : " + key;
                    else
                        throw new RuntimeException("Error occurred");
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
    @PostMapping("/logout/{username}/{apikey}")
    public String logout(@PathVariable String username, @PathVariable String apikey) {
        Customer c = db.getCustomer(username);
        if (c != null) {
            if(c.getApiKey() == null){
                throw new UnauthorizedException("Please login");
            }
            if(!c.getApiKey().equals(apikey)){
                throw new UnauthorizedException("Unauthenticated - incorrect API Key.");
            }
            if (db.endSession(c) == 1) {
                return "Logged out successfully.";
            } else
                throw new RuntimeException("Error occurred.");
        } else {
            throw new ResourceNotFoundException("Invalid user");
        }
    }

    @PostMapping("/view")
    public Object getProperty(@RequestBody SearchPropertyRequest searchPropertyRequest) throws Exception {
        if(searchPropertyRequest.getCity()!=null && searchPropertyRequest.getCheckIn()!=null && searchPropertyRequest.getCheckOut()!=null) {

            //Validate the check-in and check-out date
            SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
            String result = validateInputDates(searchPropertyRequest.getCheckIn(), searchPropertyRequest.getCheckOut(), sdf);

            if(result != null){
                throw new InvalidRequestException(result);
            }

            //Get the list of properties based on city
            List<RentalProperty> propertiesList = db.getProperties(searchPropertyRequest);

            if(propertiesList.size() == 0)
            {
                throw new Exception("City doesn't contain data and/or Invalid city");
            }

            //Get the reservations from reservation table
            List<Reservation> reservationsList = db.getReservations();

            SearchPropertyService searchPropertyService = new SearchPropertyService();
            searchPropertyService.assignPropertiesList(propertiesList,sdf.parse(searchPropertyRequest.getCheckIn()), sdf.parse(searchPropertyRequest.getCheckOut()));

            //Verify Properties with Reservation Table
            propertiesList = searchPropertyService.verifyProperties(reservationsList);

            //Setting the owner_name, owner_email and owner_phone_number
            for(RentalProperty rentalProperty:propertiesList) {
                Customer customer = db.getCustomerByID(rentalProperty.getOwner_id());
                if (customer != null)
                {
                    rentalProperty.setOwner_name(customer.getName());
                    rentalProperty.setOwner_email(customer.getEmail());
                    rentalProperty.setOwner_phone_number(customer.getPhone_number());
                }
                else
                {
                    throw new ResourceNotFoundException("User not found");
                }

            }

            //Filter the Properties List based on filters provided
            List<RentalProperty> filteredRentalProperties = new ArrayList<>();

            Criteria criteriaAverageRating = new CriteriaAverageRating();
            Criteria criteriaCarpetArea = new CriteriaCarpetArea();
            Criteria criteriaNumberOfBathrooms = new CriteriaNumberOfBathrooms();
            Criteria criteriaNumberOfBedrooms = new CriteriaNumberOfBedrooms();
            Criteria criteriaPetFriendly = new CriteriaPetFriendly();
            Criteria criteriaPricePerNight = new CriteriaPricePerNight();
            Criteria criteriaPropertyType = new CriteriaPropertyType();
            Criteria criteriaWifiAvailability = new CriteriaWifiAvailability();

            if(searchPropertyRequest.getAverage_rating() != null)
            {
                propertiesList = criteriaAverageRating.meetCriteria(propertiesList, searchPropertyRequest);
            }
            if(searchPropertyRequest.getCarpet_area() != null)
            {
                propertiesList = criteriaCarpetArea.meetCriteria(propertiesList, searchPropertyRequest);
            }
            if(searchPropertyRequest.getNum_baths() != null)
            {
                propertiesList = criteriaNumberOfBathrooms.meetCriteria(propertiesList, searchPropertyRequest);
            }
            if(searchPropertyRequest.getNum_bedrooms() != null)
            {
                propertiesList = criteriaNumberOfBedrooms.meetCriteria(propertiesList, searchPropertyRequest);
            }
            if(searchPropertyRequest.getPet_friendly() != null)
            {
                propertiesList = criteriaPetFriendly.meetCriteria(propertiesList, searchPropertyRequest);
            }
            if(searchPropertyRequest.getPrice_per_night() != null)
            {
                propertiesList = criteriaPricePerNight.meetCriteria(propertiesList, searchPropertyRequest);
            }
            if(searchPropertyRequest.getProperty_type() != null)
            {
                propertiesList = criteriaPropertyType.meetCriteria(propertiesList, searchPropertyRequest);
            }
            if(searchPropertyRequest.getWifi_avail() != null)
            {
                propertiesList = criteriaWifiAvailability.meetCriteria(propertiesList, searchPropertyRequest);
            }

            return propertiesList;
        }
        throw  new InvalidRequestException("Missing input values");
    }

    @PostMapping("/generateInvoice/{uname}")
    public float generateInvoice(@RequestBody(required = false) CouponList c, @PathVariable String uname)
    {
        List<Float> couponDiscounts = null;
        if (c.getCoupons()!= null)
        {
            couponDiscounts = new ArrayList<>();
            ArrayList<String> coupons = checkCoupons(c.getCoupons());
            for (int i = 0; i < coupons.size(); i++)
            {
                couponDiscounts.add(Constants.getCoupons().get(coupons.get(i)));

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
    //Private helper method to verify coupons passed in by user
    private ArrayList<String> checkCoupons(List<Coupon> coupons)
    {
        HashSet<String> allCoupons = new HashSet<>();
        for (int i = 0; i < coupons.size(); i++)
        {
            if (allCoupons.contains(coupons.get(i).getCouponCode()))
            {
                throw new InvalidRequestException("Coupon " + coupons.get(i).getCouponCode() + " already added");
            }
            if (!Constants.getCoupons().containsKey(coupons.get(i).getCouponCode()))
            {
                throw new ResourceNotFoundException("Coupon not found");
            }
            allCoupons.add(coupons.get(i).getCouponCode());
        }
       return new ArrayList<>(allCoupons);

    }

    //Private helper method for generateInvoice
    private Cart getCustomerCart(String uname)
    {
        Customer customer = db.getCustomer(uname);
        if (customer == null)
        {
            throw new ResourceNotFoundException("User not found");
        }
        Cart customerCart = customer.getCart();
        return customerCart;
    }

    @PostMapping("/cart/add/{apikey}")
    public String addToCart(@RequestBody CartRequest c, @PathVariable String apikey) {
        // Validate user
        Customer user = db.getCustomer(c.getUsername());
        if(user == null){
            throw new InvalidRequestException("Invalid user");
        }
        if(user.getApiKey() == null){
            throw new UnauthorizedException("Please login");
        }
        if(!user.getApiKey().equals(apikey)){
            throw new UnauthorizedException("Unauthenticated - incorrect API Key.");
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

    @PostMapping("/cart/remove/{apikey}")
    public String removeFromCart(@RequestBody CartRequest c, @PathVariable String apikey){
        // validate user
        Customer user = db.getCustomer(c.getUsername());
        if(user == null){
            throw new InvalidRequestException("Invalid user");
        }
        if(user.getApiKey() == null){
            throw new UnauthorizedException("Please login");
        }
        if(!user.getApiKey().equals(apikey)){
            throw new UnauthorizedException("Unauthenticated - incorrect API Key.");
        }
        Cart cart = user.getCart();
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
    @GetMapping(path = "/cart/view/{username}/{apikey}", produces= MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> viewCart(@PathVariable String username, @PathVariable String apikey) {
        Customer user = db.getCustomer(username);
        if(user == null){
            throw new ResourceNotFoundException("Invalid User");
        }
        if(user.getApiKey() == null){
            throw new UnauthorizedException("Please login");
        }
        if(!user.getApiKey().equals(apikey)){
            throw new UnauthorizedException("Unauthenticated - incorrect API Key.");
        }
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

    private void validateProperty(int propertyID){
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

    @PostMapping("/reserve/{apikey}")
    public String createReservation(@RequestBody ReservationRequest r, @PathVariable String apikey) {
        String username = r.getUsername();
        Customer user = db.getCustomer(username);
        if(user == null){
            throw new ResourceNotFoundException("Invalid User");
        }
        if(user.getApiKey() == null){
            throw new UnauthorizedException("Please login");
        }
        if(!user.getApiKey().equals(apikey)){
            throw new UnauthorizedException("Unauthenticated - incorrect API Key.");
        }
        int userId = user.getUserID();
        Cart userCart = user.getCart();
        ArrayList<Reservation> reservationList = db.getReservations();
        if(!userCart.verifyCart(reservationList)){
            throw new InvalidRequestException("One or more properties in the cart are unavailable.");
        }
        ArrayList<RentalProperty> property_list = userCart.getProperty();
        ArrayList<Date> checkinDates = userCart.getCheckinDate();
        ArrayList<Date> checkoutDates = userCart.getCheckoutDate();
        CouponList cL = new CouponList();
        List<Coupon> coupons = r.getCoupons();
        cL.setCoupons(coupons);
        int size = property_list.size();
        ArrayList<Reservation> reservations = new ArrayList<Reservation>();
        Random rand = new Random();
        int resID = rand.nextInt(1000);
        float invoiceAmount = generateInvoice(cL,username);
        for(int i = 0; i < size; i++) {
            SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
            int propertyId = property_list.get(i).getProperty_id();
            Date checkinDate = checkinDates.get(i);
            Date checkoutDate = checkoutDates.get(i);
            RentalProperty p = db.getProperty(propertyId);
            Reservation reserve = new Reservation();
            reserve.setConfirmationNumber(resID);
            reserve.setCustomer(user);
            reserve.setProperty(p);
            reserve.setCheckinDate(checkinDate);
            reserve.setCheckoutDate(checkoutDate);
            reserve.setInvoiceAmount(invoiceAmount);
            reservations.add(reserve);
        }
        int result = db.makeReservation(reservations);
        if(result == -1){
            throw new RuntimeException("Error occurred, couldn't reserve");
        }else{
            clearCart(user, apikey);
            return "Reserved Successfully, Confirmation number is " + result;
        }
    }

    private void clearCart(Customer user, String apikey){
        Cart userCart = user.getCart();
        SimpleDateFormat sdf =  new SimpleDateFormat("MM-dd-yyyy");
        for(int i=0;i<userCart.getProperty().size();i++){
            CartRequest cr = new CartRequest();
            cr.setUsername(user.getUsername());
            cr.setPropertyID(userCart.getProperty().get(i).getProperty_id());
            cr.setCheckinDate(sdf.format(userCart.getCheckinDate().get(i)));
            cr.setCheckoutDate(sdf.format(userCart.getCheckoutDate().get(i)));
            removeFromCart(cr, apikey);
        }
    }
    @PostMapping("/rate/{confirmationNumber}/{rating}")
    public static void rate_property(@PathVariable String confirmationNumber , @PathVariable Float rating) {

    }
    @PostMapping("/hostProperty/{username}/{apikey}")
    public String hostProperty(@RequestBody HostPropertyRequest hp, @PathVariable String username, @PathVariable String apikey){
        // Validate user
        Customer user =  db.getCustomer(username);
        if(user == null){
            throw new ResourceNotFoundException("Invalid user.");
        }
        if(user.getApiKey() == null){
            throw new UnauthorizedException("Please login");
        }
        if(!user.getApiKey().equals(apikey)){
            throw new UnauthorizedException("Unauthenticated - incorrect API Key.");
        }

        // This would be Villa, Resort, BeachHouse, Apartment, Studio, Motel
        String propertyType = hp.getProperty_type().toLowerCase();

        // Validation
        if(Constants.getPropertyClass().containsKey(propertyType)){
            FactoryProducer producer = FactoryProducer.getInstance();
            PropertyFactory factory = producer.getFactory(Constants.getPropertyClass().get(propertyType));
            RentalProperty property = factory.getProperty(propertyType);
            property = setProperty(property, hp, user.getUserID());

            if(db.save(property) == 1)
                return "Hosted property successfully.";
            throw new RuntimeException("Could not host property.");
        }
        else
            throw new InvalidRequestException("Property type should belong to (Villa, BeachHouse, Resort, Apartment, Studio, House, Motel).");
    }

    private RentalProperty setProperty(RentalProperty property, HostPropertyRequest hp, int ownerID){
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
        property.setAverage_rating(0f);
        property.setOwner_id(ownerID);
        property.setAvailability(hp.getAvailability());

        return property;
    }

}
