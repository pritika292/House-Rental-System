package com.reservation.rentaplace.Controller;
import com.nimbusds.jose.shaded.json.JSONObject;
import com.reservation.rentaplace.Criteria.*;
import com.reservation.rentaplace.Domain.*;
import com.reservation.rentaplace.DAO.DBMgr;
import com.reservation.rentaplace.Domain.Command.Coupon;
import com.reservation.rentaplace.Domain.Command.CouponList;
import com.reservation.rentaplace.Domain.Command.InvoiceGenerator;
import com.reservation.rentaplace.Domain.Request.*;
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

    }
    @PostMapping("/logout/{username}/{apikey}")
    public String logout(@PathVariable String username, @PathVariable String apikey) {
        Customer c = authenticateUser(username,apikey);
        if(c == null){
            throw new UnauthorizedException("Unauthorized or Invalid user");
        }
        if (db.endSession(c) == 1) {
            return "Logged out successfully.";
        } else
            throw new RuntimeException("Error occurred.");
    }

    @PostMapping("/view")
    public Object getProperty(@RequestBody SearchPropertyRequest searchPropertyRequest) throws Exception {
        if(searchPropertyRequest.getCity()!=null && searchPropertyRequest.getCheckIn()!=null && searchPropertyRequest.getCheckOut()!=null) {

            //Validate the check-in and check-out date
            SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
            String result = validateInputDates(searchPropertyRequest.getCheckIn(), searchPropertyRequest.getCheckOut(), sdf);

            if((result != null) && (result.compareTo("Valid dates") != 0)) {
                throw new InvalidRequestException(result);
            }

            //Get the list of properties based on city
            List<RentalProperty> propertiesList = db.getProperties(searchPropertyRequest);

            if(propertiesList == null || propertiesList.size() == 0)
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

    @PostMapping("/generateInvoice/{uname}/{apiKey}")
    public float generateInvoice(@RequestBody(required = false) CouponList c, @PathVariable String uname, @PathVariable String apiKey)
    {
        Customer customer = authenticateUser(uname, apiKey);
        if (customer == null)
        {
            throw new UnauthorizedException("Unauthorized or Invalid user");
        }
        List<Float> couponDiscounts = null;
        if (c!=null && c.getCoupons()!= null)
        {
            couponDiscounts = new ArrayList<>();
            ArrayList<String> coupons = checkCoupons(c.getCoupons());
            for (int i = 0; i < coupons.size(); i++)
            {
                couponDiscounts.add(Constants.getCoupons().get(coupons.get(i)));

            }
        }
        Cart customerCart = customer.getCart();
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

    //Helper Method to authenticate user
    private Customer authenticateUser(String username, String apiKey)
    {
        Customer customer = db.getCustomer(username);
        if (customer == null || customer.getApiKey() == null || !customer.getApiKey().equals(apiKey))
        {
            return null;
        }
        return customer;
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



    @GetMapping(path="getPastReservations/owner/{uname}/{apiKey}", produces= MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getPastReservationofOwner(@PathVariable String uname, @PathVariable String apiKey) {
        Customer user = authenticateUser(uname, apiKey);
        if (user == null) {
            throw new UnauthorizedException("Unauthorized or Invalid user");
        }
        List<Reservation> userReservations = new ArrayList<>();
        List<Reservation> reservations = db.getReservations();
        for (Reservation r : reservations) {
            if (r.getProperty().getOwner_id() == user.getUserID()) {
                userReservations.add(r);
            }
        }
        if (userReservations.size() == 0)
        {
            return new ResponseEntity<Object>("No reservations for this user's property or user does not have properties hosted", HttpStatus.OK);
        }
        List<JSONObject> entities = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
        for (int i = 0; i < userReservations.size(); i++) {
            JSONObject entity = new JSONObject();
            entity.put("Confirmation Number", userReservations.get(i).getConfirmationNumber());
            entity.put("Property Name", userReservations.get(i).getProperty().getProperty_name());
            entity.put("Checkin date", sdf.format(userReservations.get(i).getCheckinDate()));
            entity.put("Checkout date", sdf.format(userReservations.get(i).getCheckoutDate()));
            entity.put("Customer Name", userReservations.get(i).getCustomer().getName());
            entity.put("Customer Phone Number", userReservations.get(i).getCustomer().getPhone_number());
            entity.put("Customer Email", userReservations.get(i).getCustomer().getEmail());
            entities.add(entity);
        }
        return new ResponseEntity<Object>(entities, HttpStatus.OK);
    }

    @PostMapping(path="getPastReservations/renter/{uname}/{apiKey}", produces= MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getPastReservationforRenter(@PathVariable String uname, @PathVariable String apiKey)
    {
        Customer user = authenticateUser(uname, apiKey);
        if (user == null)
        {
            throw new UnauthorizedException("Unauthorized or Invalid user");
        }

        List<Reservation> reservations = db.getReservations();
        HashMap<Integer, UserReservation> groupedReservations = getRenterReservations(reservations);
        List<UserReservation> userReservations = new ArrayList<>();
        for (Integer key: groupedReservations.keySet())
        {
            if (groupedReservations.get(key).getCustomer().getUserID() == user.getUserID())
            {
                userReservations.add(groupedReservations.get(key));
            }
        }
        if (userReservations.size() == 0)
        {
            return new ResponseEntity<Object>("User has no reservations", HttpStatus.OK);
        }
        System.out.println(userReservations.size());
        List<JSONObject> entities = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
        for (int i = 0; i < userReservations.size(); i++)
        {
            JSONObject entity = new JSONObject();
            entity.put("Confirmation Number", userReservations.get(i).getConfirmationNumber());
            String properties = "";
            int index = 0;
            List<JSONObject> propertyObjects = new ArrayList<>();
            for (RentalProperty p: userReservations.get(i).getPropertyIds())
            {
                JSONObject obj = new JSONObject();
                JSONObject propertyDetails = new JSONObject();
                propertyDetails.put("Property ID", p.getProperty_id());
                propertyDetails.put("Price per night", p.getPrice_per_night());
                propertyDetails.put("Number of bedrooms", p.getNum_bedrooms());
                propertyDetails.put("Number of bathrooms", p.getNum_baths());
                propertyDetails.put("Property description", p.getProperty_description());
                propertyDetails.put("Property name", p.getProperty_name());
                propertyDetails.put("Property type", p.getProperty_type());
                propertyDetails.put("City",p.getCity());
                propertyDetails.put("Carpet area", p.getCarpet_area());
                String res = "No";
                if(p.getPet_friendly() == 1)
                    res = "Yes";
                propertyDetails.put("Pet friendly", res);
                res = "No";
                if(p.getWifi_avail() == 1)
                    res = "Yes";
                propertyDetails.put("Wifi", res);
                propertyDetails.put("Rating", p.getAverage_rating());
                obj.put("Property information", propertyDetails);
                //obj.put("Property Info", p);
                String inDate = sdf.format(userReservations.get(i).getCheckinDate().get(index));
                String outDate = sdf.format(userReservations.get(i).getCheckoutDate().get(index));
                obj.put("Checkin Date", inDate);
                obj.put("Checkout Date", outDate);
                propertyObjects.add(obj);
                index+=1;
            }
            entity.put("Properties", propertyObjects);
            entity.put("Customer Name", userReservations.get(i).getCustomer().getName());
            entity.put("Invoice Amount", userReservations.get(i).getInvoice_amount());
            entity.put("Customer Email", userReservations.get(i).getCustomer().getEmail());
            entities.add(entity);
        }


        return new ResponseEntity<Object>(entities, HttpStatus.OK);
    }

    private HashMap<Integer, UserReservation> getRenterReservations(List<Reservation> reservations)
    {
        UserReservation u1 = null;
        HashMap<Integer, UserReservation> userReservations = new HashMap<>();
        for (Reservation r: reservations)
        {
            if (!userReservations.containsKey(r.getConfirmationNumber()))
            {
                Integer confNumber = r.getConfirmationNumber();
                u1 = new UserReservation();
                List<RentalProperty> properties = new ArrayList<>();
                List<Date> checkInDates = new ArrayList<>();
                List<Date> checkOutDates = new ArrayList<>();
                properties.add(r.getProperty());
                checkInDates.add(r.getCheckinDate());
                checkOutDates.add(r.getCheckoutDate());
                u1.setCheckinDate(checkInDates);
                u1.setCheckoutDate(checkOutDates);
                u1.setPropertyIds(properties);
                u1.setConfirmationNumber(confNumber);
                u1.setCustomer(r.getCustomer());
                u1.setInvoice_amount(r.getInvoiceAmount());
                userReservations.put(confNumber, u1);
            }
            else
            {
                u1 = userReservations.get(r.getConfirmationNumber());
                List<RentalProperty> properties = u1.getPropertyIds();
                List<Date> checkInDates = u1.getCheckinDate();
                List<Date> checkOutDates = u1.getCheckoutDate();
                properties.add(r.getProperty());
                checkInDates.add(r.getCheckinDate());
                checkOutDates.add(r.getCheckoutDate());
                u1.setPropertyIds(properties);
                u1.setCheckinDate(checkInDates);
                u1.setCheckoutDate(checkOutDates);
                u1.setCustomer(r.getCustomer());
                userReservations.put(r.getConfirmationNumber(), u1);
            }

        }
        return userReservations;
    }

    @PostMapping("/cart/add/{apikey}")
    public String addToCart(@RequestBody CartRequest c, @PathVariable String apikey) {

        //Changed to authenticate user in one step
        Customer user = authenticateUser(c.getUsername(), apikey);
        if (user == null)
        {
            throw new UnauthorizedException("Unauthorized or Invalid user");
        }
        // Validate property id
        int propertyID = c.getPropertyID();
        validateProperty(propertyID);
        // Validate dates
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
        String result = validateInputDates(c.getCheckinDate(), c.getCheckoutDate(), sdf);
        if(result == null || !result.equals("Valid dates")){
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
        //Changed to authenticate user in one step
        Customer user = authenticateUser(c.getUsername(), apikey);
        if (user == null)
        {
            throw new UnauthorizedException("Unauthorized or Invalid user");
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
        //Changed to authenticate user in one step
        Customer user = authenticateUser(username, apikey);
        if (user == null)
        {
            throw new UnauthorizedException("Unauthorized or Invalid user");
        }
        Cart cart = user.getCart();
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
        List<JSONObject> entities = new ArrayList<JSONObject>();
        for (int i=0;i<cart.getProperty().size();i++) {
            JSONObject entity = new JSONObject();
            JSONObject propertyDetails = new JSONObject();
            propertyDetails.put("Property ID", cart.getProperty().get(i).getProperty_id());
            propertyDetails.put("Price per night", cart.getProperty().get(i).getPrice_per_night());
            propertyDetails.put("Number of bedrooms", cart.getProperty().get(i).getNum_bedrooms());
            propertyDetails.put("Number of bathrooms", cart.getProperty().get(i).getNum_baths());
            propertyDetails.put("Property description", cart.getProperty().get(i).getProperty_description());
            propertyDetails.put("Property name", cart.getProperty().get(i).getProperty_name());
            propertyDetails.put("Property type", cart.getProperty().get(i).getProperty_type());
            propertyDetails.put("City", cart.getProperty().get(i).getCity());
            propertyDetails.put("Carpet area", cart.getProperty().get(i).getCarpet_area());
            String res = "No";
            if(cart.getProperty().get(i).getPet_friendly() == 1)
                res = "Yes";
            propertyDetails.put("Pet friendly", res);
            res = "No";
            if(cart.getProperty().get(i).getWifi_avail() == 1)
                res = "Yes";
            propertyDetails.put("Wifi", res);
            propertyDetails.put("Rating", cart.getProperty().get(i).getAverage_rating());
            //entity.put("Property", cart.getProperty().get(i));
            entity.put("Property", propertyDetails);
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
    private String validateInputDates(String checkinDate, String checkoutDate, SimpleDateFormat sdf) {
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
            return "Valid dates";
        }
        catch(Exception e){
            return null;
        }

    }

    @PostMapping("/reserve/{apikey}")
    public String createReservation(@RequestBody ReservationRequest r, @PathVariable String apikey) {
        String username = r.getUsername();
        Customer user = authenticateUser(username,apikey);
        if(user == null){
            throw new UnauthorizedException("Unauthorized or Invalid user");
        }
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
        float invoiceAmount = generateInvoice(cL,username, user.getApiKey());
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

    @PostMapping("/hostProperty/{username}/{apikey}")
    public String hostProperty(@RequestBody HostPropertyRequest hp, @PathVariable String username, @PathVariable String apikey){
        // Validate user
        Customer user =  authenticateUser(username, apikey);
        if (user == null)
        {
            throw new UnauthorizedException("Unauthorized or Invalid user");
        }
        // This would be Villa, Resort, BeachHouse, Apartment, Studio, Motel
        String propertyType = hp.getProperty_type().toLowerCase();

        // Validation
        if(Constants.getPropertyClass().containsKey(propertyType)){
            RentalProperty property = db.getsetProperty(propertyType);
            property = setProperty(property, hp, user.getUserID());

            if(db.hostProperty(property) == 1)
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
        property.setNumber_of_reviews(0);
        property.setAvailability(hp.getAvailability());

        return property;
    }

    @PostMapping("/rateProperty/{username}/{apiKey}")
    public String rateProperty(@RequestBody RatePropertyRequest rp, @PathVariable String username, @PathVariable String apiKey) throws ParseException {
        // User validation based on username and apiKey
        Customer user =  authenticateUser(username,apiKey);
        if(user == null)
            throw new UnauthorizedException("Unauthorized or Invalid user");

        // Check if reservation id is valid
        Integer reservationID = rp.getReservationID();
        ArrayList<Reservation> reservations = db.getReservations(reservationID);
        if(reservations == null)
            throw new InvalidRequestException("Invalid Reservation ID");

        // If username doesn't match the username under reservation
        Customer customer = reservations.get(0).getCustomer();
        if(!customer.getUsername().equalsIgnoreCase(username))
            throw new UnauthorizedException("Reservation does not belong to user!");

        // Fetch all the properties, check-out dates and customer name from List of reservations
        ArrayList<Integer> propertyIDs = new ArrayList<>();
        ArrayList<Date> checkOutDates = new ArrayList<>();
        for(Reservation r:reservations){
            propertyIDs.add(r.getProperty().getProperty_id());
            checkOutDates.add(r.getCheckoutDate());
        }

        // check if property id in the request body is valid
        RentalProperty property = db.getProperty(rp.getPropertyID());
        if(!propertyIDs.contains(property.getProperty_id()))
            throw new InvalidRequestException("Property ID does not belong to reservation.");

        // check if today's date is on or after the checkout date of property
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy");
        String today = sdf.format(new Date());
        Date todaysDate = sdf.parse(today);

        int property_index = propertyIDs.indexOf(property.getProperty_id());
        Date checkOutDate = checkOutDates.get(property_index);
        if (todaysDate.compareTo(checkOutDate) <1)
            throw new InvalidRequestException("Too soon to rate property. Wait until check-out date");


        // Verify if review lies between 0 and 5
        double rate = rp.getRating();
        if (0 <= rate && rate <= 5) {
            // form new rating and update in DB
            double avgRating = property.getAverage_rating();
            int numberOfReviews = property.getNumber_of_reviews();
            float newAvgRating = (float) ((avgRating * numberOfReviews + rate) / (++numberOfReviews));
            property.setAverage_rating(newAvgRating);
            property.setNumber_of_reviews(numberOfReviews);

            if (db.saveRating(property.getProperty_id(), newAvgRating, numberOfReviews) == 1)
                return "Thank you for your review!";
            else
                throw new RuntimeException("Could not rate property.");
        }
        else
            throw new InvalidRequestException("Rating must lie between 0 and 5.");
    }
}
