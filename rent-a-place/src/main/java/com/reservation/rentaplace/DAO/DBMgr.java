package com.reservation.rentaplace.DAO;
import com.reservation.rentaplace.Domain.*;
import com.reservation.rentaplace.Domain.Factory.FactoryProducer;
import com.reservation.rentaplace.Domain.Factory.PropertyFactory;
import com.reservation.rentaplace.Domain.Request.CustomerRequest;
import com.reservation.rentaplace.mapper.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import java.util.UUID;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;

@Getter
@Setter
@Repository
public class DBMgr implements DBMgrDAO
{
    @Autowired
    public JdbcTemplate jdbcTemplate;
    private static DBMgr instance;

    public static DBMgr getInstance(){
        if(instance == null){
            instance = new DBMgr();
        }
        return instance;
    }
    public String generateMD5Hashvalue(String userName)
    {
        Date dateObj = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");
        String date = formatter.format(dateObj);
        System.out.println(date);
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("MD5");
        }
        catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException(e);
        }
        String secretPhase = "project";
        // By using the current date, userName(emailId) and
        // the secretPhase , it is generated
        byte[] hashResult
                = md.digest((date + userName + secretPhase)
                .getBytes(UTF_8));
        // convert the value to hex
        String password = bytesToHex(hashResult);
        System.out.println("Generated password.."
                + password);

        return password;
    }
    private String bytesToHex(byte[] bytes)
    {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
    @Override
    public Customer getCustomer(String uname)
    {
        String query = "select * from CUSTOMER WHERE username = ?";
        try {
            Customer c = jdbcTemplate.queryForObject(query, new CustomerRowMapper(), uname);
            System.out.println("User not null");
            Cart cart = getCart(c.getUserID());
            System.out.println(cart.getCartID());
            c.setCart(cart);
            return c;
        }
        catch (Exception e) {
            System.out.println("User is null");
            return null;
        }
    }

    @Override
    public Customer getCustomerByID(int uid)
    {
        String query = "select * from CUSTOMER WHERE customer_id = ?";
        try{
            Customer c = jdbcTemplate.queryForObject(query, new CustomerRowMapper(), uid);
            System.out.println("User not null");
            Cart cart = getCart(c.getUserID());
            c.setCart(cart);
            return c;
        }
        catch (Exception e) {
            System.out.println("User is null");
            return null;
        }
    }
    @Override
    public int endSession(Customer c){
        int userID = c.getUserID();
        String query = "UPDATE Customer SET apiKey = ? where customer_id = ?";
        try{
            jdbcTemplate.update(query, new Object[]{null, userID});
        }
        catch (Exception e){
            System.out.println(e);
            return -1;
        }
        return 1;
    }

    @Override
    public List<RentalProperty> getProperties(SearchPropertyRequest searchPropertyRequest)
    {
        String query = "select * from Property WHERE city = ? and availability='1'";
        List<RentalProperty> rentalProperty =  null;
        try {
            rentalProperty = jdbcTemplate.query(query, new SearchRequestPropertyRowMapper(), searchPropertyRequest.getCity());
            jdbcTemplate.query(query, new PropertyRowMapper(), searchPropertyRequest.getCity());

            System.out.println("not null");
            return rentalProperty;
        }
        catch (Exception e) {
            System.out.println("is null");
            return null;
        }
    }

    @Override
    public RentalProperty getProperty(Integer propertyID) {
        String propertyType = checkProperty(propertyID);
        FactoryProducer producer = FactoryProducer.getInstance();
        PropertyFactory factory = producer.getFactory(Constants.getPropertyClass().get(propertyType));
        RentalProperty property = factory.getProperty(propertyType);
        String query = "SELECT * from Property where property_id = (?)";
        try{
            PropertyRow p = jdbcTemplate.queryForObject(query, new PropertyRowMapper(), propertyID);
            property.setProperty_id(p.getProperty_id());
            property.setPrice_per_night(p.getPrice_per_night());
            property.setNum_bedrooms(p.getNum_of_bedrooms());
            property.setNum_baths(p.getNum_of_bathrooms());
            property.setProperty_description(p.getProperty_description());
            property.setProperty_name(p.getProperty_name());
            property.setProperty_type(p.getProperty_type());
            property.setCity(p.getCity());
            property.setPet_friendly(p.getPet_friendly());
            property.setWifi_avail(p.getWifi_avail());
            property.setCarpet_area(p.getCarpet_area());
            property.setAverage_rating(p.getAvg_rating());
            property.setOwner_id(p.getOwner_id());
            property.setAvailability(p.getAvailability());
            return property;
        }
        catch(Exception e){
            System.out.println(e);
            return null;
        }
    }
    @Override
    public String checkProperty(Integer property_id){
        String query = "select property_type from Property WHERE property_id = ?";
        try{
            String property_type = (String)jdbcTemplate.queryForObject(query, String.class, property_id);
            return property_type;
        }
        catch (Exception e){
            System.out.println(e);
            return null;
        }
    }
    @Override
    public ArrayList<Reservation> getReservations(){
        ArrayList<Reservation> reservations = new ArrayList<Reservation>();
        String query = "SELECT * from Reservation";
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
            List<ReservationRow> p = jdbcTemplate.query(query, new ReservationRowMapper());
            for(int i=0;i<p.size();i++){
                Reservation r = new Reservation();
                Customer c = getCustomerByID(p.get(i).getCustomer_id());
                RentalProperty property = getProperty(p.get(i).getProperty_id());
                r.setConfirmationNumber((p.get(i).getReservation_id()));
                r.setCustomer(c);
                r.setCheckinDate(sdf.parse(p.get(i).getCheckin_date()));
                r.setCheckoutDate(sdf.parse(p.get(i).getCheckout_date()));
                r.setProperty(property);
                r.setInvoiceAmount(p.get(i).getInvoice_amount());
                reservations.add(r);
            }
            return reservations;
        }
        catch (Exception e){
            System.out.println(e);
            return null;
        }
    }

    public Reservation getReservation(int reservation_id){
        try{
            SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
            ReservationRow reservationRow  = jdbcTemplate.queryForObject("SELECT * from Reservation where reservation_id = (?)", new ReservationRowMapper(), reservation_id);
            Reservation r = new Reservation();
            r.setProperty(getProperty(reservationRow.getProperty_id()));
            r.setCustomer(getCustomerByID(reservationRow.getCustomer_id()));
            r.setConfirmationNumber(reservationRow.getReservation_id());
            r.setCheckinDate(sdf.parse(reservationRow.getCheckin_date()));
            r.setCheckoutDate(sdf.parse(reservationRow.getCheckout_date()));
            return r;
        }catch(Exception e){
            System.out.println(e);
            return null;
        }
    }
    @Override
    public int makeReservation(ArrayList<Reservation> reservations){
        try{
            String query = "INSERT into Reservation(reservation_id,property_id,customer_id,checkin_date, checkout_date,invoice_amount) VALUES (?,?,?,?,?,?)";
            int res_ID = 0;
            for(int i = 0; i < reservations.size(); i++){
                SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
                Reservation r = reservations.get(i);
                int propertyId = r.getProperty().getProperty_id();
                res_ID = r.getConfirmationNumber();
                float invoiceAmount = r.getInvoiceAmount();
                String checkinDate = sdf.format(r.getCheckinDate());
                String checkoutDate = sdf.format(r.getCheckoutDate());
                int userID = r.getCustomer().getUserID();
                jdbcTemplate.update(query, new Object[] {res_ID,propertyId,userID,checkinDate,checkoutDate,invoiceAmount});
            }
            return res_ID;
        }catch (Exception e){
            System.out.println(e);
            return -1;
        }
    }

    @Override
    public int createCart(){
        String insert_sql = "INSERT INTO Cart (property_ids, cart_value) VALUES (?, ?)";
        try{
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(
                    new PreparedStatementCreator() {
                        public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                            PreparedStatement ps =
                                    connection.prepareStatement(insert_sql, new String[] {"id"});
                            ps.setString(1, "");
                            ps.setFloat(2,0);
                            return ps;
                        }
                    },
                    keyHolder);
            return keyHolder.getKey().intValue();
        }
        catch (Exception e){
            System.out.println(e);
            return -1;
        }
    }
    @Override
    public Cart getCart(int user_id){
        try{
            CartRow cartrow  = jdbcTemplate.queryForObject("SELECT * from Cart where cart_id in (SELECT cart_id from Customer where customer_id = (?))", new CartRowMapper(), user_id);
            Cart cart = new Cart();

            String[] property_ids;
            String[] checkin_dates;
            String[] checkout_dates;
            ArrayList<RentalProperty> property = new ArrayList<RentalProperty>();
            ArrayList<Date> checkinDate = new ArrayList<Date>();
            ArrayList<Date> checkoutDate = new ArrayList<Date>();
            if(cartrow.getProperty_ids() !="" && cartrow.getCheckout_date() != null && cartrow.getCheckout_date()!=null){
                property_ids = cartrow.getProperty_ids().split(",");
                checkin_dates =  cartrow.getCheckin_date().split(",");
                checkout_dates =  cartrow.getCheckout_date().split(",");
                SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
                if(property_ids.length == 0){
                    property.add(getProperty(Integer.parseInt(cartrow.getProperty_ids())));
                    checkinDate.add(sdf.parse(cartrow.getCheckin_date()));
                    checkoutDate.add(sdf.parse(cartrow.getCheckout_date()));
                }
                for(int i=0;i<property_ids.length;i++){
                    property.add(getProperty(Integer.parseInt(property_ids[i])));
                    checkinDate.add(sdf.parse(checkin_dates[i]));
                    checkoutDate.add(sdf.parse(checkout_dates[i]));
                }
            }

            cart.setCartID(cartrow.getCart_id());
            cart.setCheckinDate(checkinDate);
            cart.setCheckoutDate(checkoutDate);
            cart.setProperty(property);
            if(cartrow.getCart_value() == null)
                cart.setCartValue(0);
            cart.setCartValue(cartrow.getCart_value());

            return cart;
        }
        catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }
    @Override
    public int updateCart(Customer c){
        String update_sql = "";
        try{
            Cart cart = c.getCart();
            ArrayList<RentalProperty> items = cart.getProperty();
            int num_items = items.size();
            String properties = "";
            String checkinDates = "";
            String checkoutDates = "";
            if(num_items !=0){
                SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
                properties = Integer.toString(items.get(0).getProperty_id());
                checkinDates = sdf.format(cart.getCheckinDate().get(0));
                checkoutDates = sdf.format(cart.getCheckoutDate().get(0));
                for(int i=1;i<cart.getProperty().size();i++){
                    properties = properties +"," + items.get(i).getProperty_id();
                    checkinDates = checkinDates + "," +sdf.format(cart.getCheckinDate().get(i));
                    checkoutDates = checkoutDates + "," +sdf.format(cart.getCheckoutDate().get(i));
                }
            }
            jdbcTemplate.update("UPDATE Cart SET property_ids = (?), checkin_date = (?), checkout_date = (?), cart_value=(?) WHERE cart_id = (?)", new Object[] {properties, checkinDates, checkoutDates, cart.getCartValue(), cart.getCartID()});
            return 1;
        }
        catch(Exception e){
            System.out.println(e);
            return -1;
        }
    }
    @Override
    public int save(CustomerRequest c, int cartId){
        try{
            jdbcTemplate.update("INSERT INTO Customer (customer_name, username, password, email, phone_number, cart_id) VALUES (?, ?, ?, ?, ?, ?)", new Object[] {c.getName(), c.getUsername(), c.getPassword(), c.getEmail(), c.getPhone_number(), cartId});
            return 1;
        }
        catch (Exception e) {
            System.out.println(e);
            return 0;
        }
    }



    @Override
    public int createSession(Customer c, String key){
        int userID = c.getUserID();
        String query = "UPDATE Customer SET apiKey = ? where customer_id = ?";
        try{
            jdbcTemplate.update(query, new Object[] {key, userID});
            return 1;
        }
        catch (Exception e) {
            System.out.println(e);
            return 0;
        }
    }
    @Override
    public int hostProperty(RentalProperty p) {
        try {
            return jdbcTemplate.update("INSERT INTO PROPERTY (price_per_night, num_of_bedrooms, num_of_bathrooms," +
                            " property_description, property_name, property_type, city, pet_friendly, wifi_avail, carpet_area," +
                            " avg_rating, owner_id, availability) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                    new Object[]{p.getPrice_per_night(), p.getNum_bedrooms(), p.getNum_baths(), p.getProperty_description(), 
                            p.getProperty_name(), p.getProperty_type(), p.getCity(), p.getPet_friendly(), p.getWifi_avail(),
                            p.getCarpet_area(), p.getAverage_rating(), p.getOwner_id(), p.getAvailability()});
        }
        catch (Exception e){
            return 0;
        }
    }

    public ArrayList<Reservation> getReservations(Integer reservationID){
        ArrayList<Reservation> reservations = new ArrayList<Reservation>();
        String query = "SELECT * from Reservation where reservation_id = ?";
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
            List<ReservationRow> p = jdbcTemplate.query(query, new ReservationRowMapper(), reservationID);
            for(int i=0;i<p.size();i++){
                Reservation r = new Reservation();
                Customer c = getCustomerByID(p.get(i).getCustomer_id());
                RentalProperty property = getProperty(p.get(i).getProperty_id());
                r.setConfirmationNumber((p.get(i).getReservation_id()));
                r.setCustomer(c);
                r.setCheckinDate(sdf.parse(p.get(i).getCheckin_date()));
                r.setCheckoutDate(sdf.parse(p.get(i).getCheckout_date()));
                r.setProperty(property);
                reservations.add(r);
            }
            return reservations;
        }
        catch (Exception e){
            System.out.println(e);
            return null;
        }
    }

    @Override
    public Integer saveRating(Integer propertyID, double newRating, Integer numberOfReviews){
        String query = "UPDATE Property set average_rating = ? number_of_reviews = ? WHERE property_id = ?";
        try{
            jdbcTemplate.update(query, new Object[] {newRating, propertyID, numberOfReviews});
            return 1;
        }
        catch (Exception e){
            System.out.println(e);
            return 0;
        }
    }

    public RentalProperty getsetProperty(String propertyType){
        FactoryProducer producer = FactoryProducer.getInstance();
        PropertyFactory factory = producer.getFactory(Constants.getPropertyClass().get(propertyType));
        return factory.getProperty(propertyType);
    }
}
