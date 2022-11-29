package com.reservation.rentaplace.Controller;

import com.nimbusds.jose.shaded.json.JSONObject;
import com.reservation.rentaplace.DAO.DBMgr;
import com.reservation.rentaplace.Domain.*;
import com.reservation.rentaplace.Domain.Factory.FirstClassFactory;
import com.reservation.rentaplace.Domain.Factory.PropertyFactory;
import com.reservation.rentaplace.Domain.Request.*;
import com.reservation.rentaplace.Exception.*;
import com.reservation.rentaplace.Domain.Command.Coupon;
import com.reservation.rentaplace.Domain.Command.CouponList;
import com.reservation.rentaplace.Domain.Request.CartRequest;
import com.reservation.rentaplace.Domain.Request.CustomerRequest;
import com.reservation.rentaplace.Domain.Request.HostPropertyRequest;
import com.reservation.rentaplace.Exception.InvalidRequestException;
import com.reservation.rentaplace.Exception.ResourceNotFoundException;
import com.reservation.rentaplace.Exception.UnauthorizedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import com.reservation.rentaplace.Domain.Request.CustomerRequest;
import com.reservation.rentaplace.Exception.InvalidRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.swing.text.html.parser.Entity;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class ControllerTest {
    @Mock
    JdbcTemplate jdbcTemplate;
    @Mock
    DBMgr db;
    @InjectMocks
    Controller c;
    @BeforeEach
    void setUp() {
        c = new Controller();
        MockitoAnnotations.openMocks(this);
        c.setDb(db);
        c.getDb().setJdbcTemplate(jdbcTemplate);
    }
    @Test
    @DisplayName("Username should not exceed length 10")
    void invalidUsernameForRegister(){
        CustomerRequest cr = new CustomerRequest();
        cr.setUsername("Garrynewuser10112");
        cr.setName("Garry");
        cr.setPassword("Garr123");
        cr.setEmail("Garry@gmail.com");
        cr.setPhone_number("967-295-2987");
        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () -> c.register(cr));
        assertEquals("Username cannot exceed the length of 10", exception.getMessage());
    }

    @Test
    @DisplayName("Email id of user should be valid")
    void invalidEmailForRegister(){
        CustomerRequest cr = new CustomerRequest();
        cr.setUsername("Garry012");
        cr.setName("Garry");
        cr.setPassword("Garr123");
        cr.setEmail("Garry.com");
        cr.setPhone_number("967-295-2987");
        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () -> c.register(cr));
        assertEquals("Invalid email id", exception.getMessage());
    }

    @Test
    @DisplayName("Phone number of user should be valid")
    void invalidPhoneForRegister(){
        CustomerRequest cr = new CustomerRequest();
        cr.setUsername("Garry012");
        cr.setName("Garry");
        cr.setPassword("Garr123");
        cr.setEmail("Garry@gmail.com");
        cr.setPhone_number("967-295-298723");
        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () -> c.register(cr));
        assertEquals("Invalid phone number", exception.getMessage());
    }
    @Test
    @DisplayName("Password of user cannot be empty")
    void invalidPasswordForRegister(){
        CustomerRequest cr = new CustomerRequest();
        cr.setUsername("Garry012");
        cr.setName("Garry");
        cr.setPassword("");
        cr.setEmail("Garry@gmail.com");
        cr.setPhone_number("967-295-2987");
        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () -> c.register(cr));
        assertEquals("Password cannot empty. Please enter valid password.", exception.getMessage());
    }
    @Test
    @DisplayName("Successful registration of user")
    void successForRegister(){
        CustomerRequest cr = new CustomerRequest();
        cr.setUsername("Garry012");
        cr.setName("Garry");
        cr.setPassword("garr123");
        cr.setEmail("Garry@gmail.com");
        cr.setPhone_number("967-295-2987");
        when(c.getDb().createCart()).thenReturn(1);
        when(c.getDb().save(cr,1)).thenReturn(1);
        assertEquals(cr.getName()+ " registered successfully.", c.register(cr));
    }

    @Test
    @DisplayName("Invalid username in Login")
    void invalidUsernameLogin(){
        Login l = new Login();
        l.setUsername("Garry012");
        l.setPassword("garr123");
        when(c.getDb().getCustomer(l.getUsername())).thenReturn(null);
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> c.login(l));
        assertEquals("Login unsuccessful - invalid username", exception.getMessage());
    }

    @Test
    @DisplayName("Invalid password in Login")
    void invalidPasswordForLogin(){
        Login l = new Login();
        l.setUsername("cherry012");
        l.setPassword("cher012");
        Customer customer = new Customer();
        customer.setUsername("cherry012");
        customer.setPassword("cher123");
        when(c.getDb().getCustomer(l.getUsername())).thenReturn(customer);
        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () -> c.login(l));
        assertEquals("Login unsuccessful - invalid password", exception.getMessage());
    }
    @Test
    @DisplayName("Successful login of user")
    void successfulLogin(){
        Login l = new Login();
        l.setUsername("cherry012");
        l.setPassword("cher123");
        Customer customer = new Customer();
        customer.setUsername("cherry012");
        customer.setPassword("cher123");
        customer.setUserID(1);
        when(c.getDb().getCustomer(customer.getUsername())).thenReturn(customer);
        when(c.getDb().generateMD5Hashvalue(customer.getUsername())).thenReturn("xxxxx");
        when(c.getDb().createSession(customer, "xxxxx")).thenReturn(1);
        assertEquals("Login successful. API Key : " + "xxxxx", c.login(l));
    }

    @Test
    @DisplayName("Invalid session for logout")
    void invalidLogout(){
        Customer customer = new Customer();
        customer.setUsername("cherry012");
        customer.setPassword("cher123");
        customer.setApiKey(null);
        when(c.getDb().getCustomer(customer.getUsername())).thenReturn(customer);
        UnauthorizedException exception = assertThrows(UnauthorizedException.class, () -> c.logout(customer.getUsername(), "xxxxx"));
        assertEquals("Please login", exception.getMessage());
    }

    @Test
    @DisplayName("Invalid user for logout")
    void invalidUserLogout(){
        when(c.getDb().getCustomer("jerry012")).thenReturn(null);
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> c.logout("jerry012", "xxxxx"));
        assertEquals("Invalid user", exception.getMessage());
    }

    @Test
    @DisplayName("Invalid API key for logout")
    void invalidAPIKeyLogout(){
        Customer customer = new Customer();
        customer.setUsername("cherry012");
        customer.setPassword("cher123");
        customer.setApiKey("yyyyy");
        when(c.getDb().getCustomer(customer.getUsername())).thenReturn(customer);
        // when(c.getDb().generateMD5Hashvalue(customer.getUsername())).thenReturn("yyyyy");
        UnauthorizedException exception = assertThrows(UnauthorizedException.class, () -> c.logout(customer.getUsername(), "xxxxx"));
        assertEquals("Unauthenticated - incorrect API Key.", exception.getMessage());
    }

    @Test
    @DisplayName("Successful logout")
    void successfulLogout(){
        Customer customer = new Customer();
        customer.setUsername("cherry012");
        customer.setPassword("cher123");
        customer.setApiKey("yyyyy");
        when(c.getDb().getCustomer(customer.getUsername())).thenReturn(customer);
        when(c.getDb().endSession(customer)).thenReturn(1);
        assertEquals("Logged out successfully.", c.logout(customer.getUsername(), "yyyyy"));
    }


    @Test
    @DisplayName("Invalid user for add to cart")
    void invalidUserAddToCart(){
        when(c.getDb().getCustomer("jerry012")).thenReturn(null);
        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () -> c.addToCart(new CartRequest(), "xxxxx"));
        assertEquals("Invalid user", exception.getMessage());
    }

    @Test
    @DisplayName("Invalid session for add to cart")
    void invalidSessionAddToCart(){
        Customer customer = getCustomer();
        customer.setApiKey(null);
        CartRequest cr = new CartRequest();
        cr.setUsername("cherry012");
        cr.setPropertyID(1);
        cr.setCheckinDate("12-12-2022");
        cr.setCheckoutDate("14-12-2022");
        when(c.getDb().getCustomer(cr.getUsername())).thenReturn(customer);
        UnauthorizedException exception = assertThrows(UnauthorizedException.class, () -> c.addToCart(cr, "xxxxx"));
        assertEquals("Please login", exception.getMessage());
    }

    @Test
    @DisplayName("Invalid API Key for add to cart")
    void invalidAPIKeyAddToCart(){
        Customer customer = getCustomer();
        customer.setApiKey("yyyyy");
        CartRequest cr = new CartRequest();
        cr.setUsername("cherry012");
        cr.setPropertyID(1);
        cr.setCheckinDate("12-12-2022");
        cr.setCheckoutDate("14-12-2022");
        when(c.getDb().getCustomer(cr.getUsername())).thenReturn(customer);
        UnauthorizedException exception = assertThrows(UnauthorizedException.class, () -> c.addToCart(cr, "xxxxx"));
        assertEquals("Unauthenticated - incorrect API Key.", exception.getMessage());
    }
//    @Test
//    @DisplayName("Invalid user for add to cart")
//    void invalidUserAddToCart(){
//        when(c.getDb().getCustomer("jerry012")).thenReturn(null);
//        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> c.addToCart(new CartRequest(), "xxxxx"));
//        assertEquals("Invalid user", exception.getMessage());
//    }
//
//    @Test
//    @DisplayName("Invalid session for add to cart")
//    void invalidSessionAddToCart(){
//        Customer customer = getCustomer();
//        customer.setApiKey(null);
//        CartRequest cr = new CartRequest();
//        cr.setUsername("cherry012");
//        cr.setPropertyID(1);
//        cr.setCheckinDate("12-12-2022");
//        cr.setCheckoutDate("14-12-2022");
//        when(c.getDb().getCustomer(cr.getUsername())).thenReturn(customer);
//        UnauthorizedException exception = assertThrows(UnauthorizedException.class, () -> c.addToCart(cr, "xxxxx"));
//        assertEquals("Please login", exception.getMessage());
//    }
//
//    @Test
//    @DisplayName("Invalid API Key for add to cart")
//    void invalidAPIKeyAddToCart(){
//        Customer customer = getCustomer();
//        customer.setApiKey("yyyyy");
//        CartRequest cr = new CartRequest();
//        cr.setUsername("cherry012");
//        cr.setPropertyID(1);
//        cr.setCheckinDate("12-12-2022");
//        cr.setCheckoutDate("14-12-2022");
//        when(c.getDb().getCustomer(cr.getUsername())).thenReturn(customer);
//        UnauthorizedException exception = assertThrows(UnauthorizedException.class, () -> c.addToCart(cr, "xxxxx"));
//        assertEquals("Unauthenticated - incorrect API Key.", exception.getMessage());
//    }

    Cart getEmptyCart(){
        Cart cart = new Cart();
        cart.setCartID(1);
        cart.setCartValue(0f);
        cart.setProperty(new ArrayList<RentalProperty>());
        cart.setCheckinDate(new ArrayList<Date>());
        cart.setCheckoutDate(new ArrayList<Date>());
        return cart;
    }
    Customer getCustomer(){
        Customer customer = new Customer();
        customer.setUsername("cherry012");
        customer.setPassword("cher123");
        customer.setApiKey("xxxxx");
        return customer;
    }
    @Test
    @DisplayName("Checkout date before checkin date in add to cart")
    void invalidDatesAddToCart(){
        Cart cart = getEmptyCart();
        Customer customer = getCustomer();
        customer.setCart(cart);
        CartRequest cr = new CartRequest();
        cr.setUsername("cherry012");
        cr.setPropertyID(1);
        cr.setCheckinDate("12-14-2022");
        cr.setCheckoutDate("12-12-2022");
        RentalProperty property = new Villa();
        property.setPrice_per_night(70f);
        when(c.getDb().getCustomer(cr.getUsername())).thenReturn(customer);
        when(c.getDb().checkProperty(cr.getPropertyID())).thenReturn("villa");
        when(c.getDb().getProperty(cr.getPropertyID())).thenReturn(property);
        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () -> c.addToCart(cr, "xxxxx"));
        assertEquals("Checkin date is after the checkout date, please select valid dates", exception.getMessage());
    }

    @Test
    @DisplayName("Invalid checkin date add to cart")
    void invalidCheckinDatesAddToCart(){
        Cart cart = getEmptyCart();
        Customer customer = getCustomer();
        customer.setCart(cart);
        CartRequest cr = new CartRequest();
        cr.setUsername("cherry012");
        cr.setPropertyID(1);
        cr.setCheckinDate("14-12-2022");
        cr.setCheckoutDate("12-12-2022");
        RentalProperty property = new Villa();
        property.setPrice_per_night(70f);
        when(c.getDb().getCustomer(cr.getUsername())).thenReturn(customer);
        when(c.getDb().checkProperty(cr.getPropertyID())).thenReturn("villa");
        when(c.getDb().getProperty(cr.getPropertyID())).thenReturn(property);
        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () -> c.addToCart(cr, "xxxxx"));
        assertEquals("Invalid check-in date", exception.getMessage());
    }

    @Test
    @DisplayName("Past checkin date add to cart")
    void pastCheckinDatesAddToCart(){
        Cart cart = getEmptyCart();
        Customer customer = getCustomer();
        customer.setCart(cart);
        CartRequest cr = new CartRequest();
        cr.setUsername("cherry012");
        cr.setPropertyID(1);
        cr.setCheckinDate("11-18-2022");
        cr.setCheckoutDate("12-12-2022");
        RentalProperty property = new Villa();
        property.setPrice_per_night(70f);
        when(c.getDb().getCustomer(cr.getUsername())).thenReturn(customer);
        when(c.getDb().checkProperty(cr.getPropertyID())).thenReturn("villa");
        when(c.getDb().getProperty(cr.getPropertyID())).thenReturn(property);
        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () -> c.addToCart(cr, "xxxxx"));
        assertEquals("Please select a future checkin date.", exception.getMessage());
    }

    @Test
    @DisplayName("Past checkout date add to cart")
    void pastCheckoutDatesAddToCart(){
        Cart cart = getEmptyCart();
        Customer customer = getCustomer();
        customer.setCart(cart);
        CartRequest cr = new CartRequest();
        cr.setUsername("cherry012");
        cr.setPropertyID(1);
        cr.setCheckinDate("12-12-2022");
        cr.setCheckoutDate("11-12-2022");
        RentalProperty property = new Villa();
        property.setPrice_per_night(70f);
        when(c.getDb().getCustomer(cr.getUsername())).thenReturn(customer);
        when(c.getDb().checkProperty(cr.getPropertyID())).thenReturn("villa");
        when(c.getDb().getProperty(cr.getPropertyID())).thenReturn(property);
        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () -> c.addToCart(cr, "xxxxx"));
        assertEquals("Please select a future checkout date.", exception.getMessage());
    }

    @Test
    @DisplayName("Invalid checkout date add to cart")
    void invalidCheckoutDatesAddToCart(){
        Cart cart = getEmptyCart();
        Customer customer = getCustomer();
        customer.setCart(cart);
        CartRequest cr = new CartRequest();
        cr.setUsername("cherry012");
        cr.setPropertyID(1);
        cr.setCheckinDate("12-12-2022");
        cr.setCheckoutDate("14-12-2022");
        RentalProperty property = new Villa();
        when(c.getDb().getCustomer(cr.getUsername())).thenReturn(customer);
        when(c.getDb().checkProperty(cr.getPropertyID())).thenReturn("villa");
        when(c.getDb().getProperty(cr.getPropertyID())).thenReturn(getProperty(property));
        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () -> c.addToCart(cr, "xxxxx"));
        assertEquals("Invalid check-out date", exception.getMessage());
    }

    @Test
    @DisplayName("Invalid property add to cart")
    void invalidPropertyAddToCart(){
        Cart cart = getEmptyCart();
        Customer customer = getCustomer();
        customer.setCart(cart);
        CartRequest cr = new CartRequest();
        cr.setUsername("cherry012");
        cr.setPropertyID(1);
        cr.setCheckinDate("12-12-2022");
        cr.setCheckoutDate("12-15-2022");
        when(c.getDb().getCustomer(cr.getUsername())).thenReturn(customer);
        when(c.getDb().checkProperty(cr.getPropertyID())).thenReturn(null);
        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () -> c.addToCart(cr, "xxxxx"));
        assertEquals("Invalid property id : "+ cr.getPropertyID(), exception.getMessage());
    }
    @Test
    @DisplayName("Successful add to cart")
    void successfulAddToCart(){
        Cart cart = getEmptyCart();
        Customer customer = getCustomer();
        customer.setCart(cart);
        CartRequest cr = new CartRequest();
        cr.setUsername("cherry012");
        cr.setPropertyID(1);
        cr.setCheckinDate("12-12-2022");
        cr.setCheckoutDate("12-14-2022");
        RentalProperty property = new Villa();
        when(c.getDb().getCustomer(cr.getUsername())).thenReturn(customer);
        when(c.getDb().checkProperty(cr.getPropertyID())).thenReturn("villa");
        when(c.getDb().getProperty(cr.getPropertyID())).thenReturn(getProperty(property));
        when(c.getDb().updateCart(customer)).thenReturn(1);
        assertEquals("Added to cart successfully", c.addToCart(cr, "xxxxx"));
    }

//    @Test
//    @DisplayName("Invalid user view cart")
//    void invalidUserViewCart(){
//        Cart cart = getEmptyCart();
//        Customer customer = getCustomer();
//        customer.setCart(cart);
//        when(c.getDb().getCustomer(customer.getUsername())).thenReturn(null);
//        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> c.viewCart(customer.getUsername(), "xxxxx"));
//        assertEquals("Invalid User", exception.getMessage());
//    }
//    @Test
//    @DisplayName("Invalid session view cart")
//    void invalidSessionViewCart(){
//        Cart cart = getEmptyCart();
//        Customer customer = getCustomer();
//        customer.setApiKey(null);
//        customer.setCart(cart);
//        when(c.getDb().getCustomer(customer.getUsername())).thenReturn(customer);
//        UnauthorizedException exception = assertThrows(UnauthorizedException.class, () -> c.viewCart(customer.getUsername(), "yyyyy"));
//        assertEquals("Please login", exception.getMessage());
//    }
//
//    @Test
//    @DisplayName("Invalid API key view cart")
//    void invalidAPIKeyViewCart(){
//        Cart cart = getEmptyCart();
//        Customer customer = getCustomer();
//        customer.setCart(cart);
//        when(c.getDb().getCustomer(customer.getUsername())).thenReturn(customer);
//        UnauthorizedException exception = assertThrows(UnauthorizedException.class, () -> c.viewCart(customer.getUsername(), "yyyyy"));
//        assertEquals("Unauthenticated - incorrect API Key.", exception.getMessage());
//    }
    @Test
    @DisplayName("Successful view empty cart")
    void successfulViewEmptyCart(){
        Cart cart = getEmptyCart();
        Customer customer = getCustomer();
        customer.setCart(cart);
        when(c.getDb().getCustomer(customer.getUsername())).thenReturn(customer);
        List<JSONObject> entities = new ArrayList<JSONObject>();
        JSONObject price = new JSONObject();
        price.put("Cart value", cart.getCartValue());
        entities.add(price);

        assertEquals(new ResponseEntity<Object>(entities, HttpStatus.OK), c.viewCart(customer.getUsername(),"xxxxx"));
    }

    @Test
    @DisplayName("Successful view cart")
    void successfulViewCart(){
        Cart cart = getEmptyCart();
        Customer customer = getCustomer();
        customer.setCart(cart);
        RentalProperty property = new Villa();
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
        try{
            cart.addToCart(getProperty(property),sdf.parse("12-12-2022"), sdf.parse("12-15-2022"));
        }
        catch(Exception e){
            System.out.println(e);
        }
        when(c.getDb().getCustomer(customer.getUsername())).thenReturn(customer);


        assertEquals(new ResponseEntity<Object>(getViewResponse(customer.getCart(), sdf), HttpStatus.OK), c.viewCart(customer.getUsername(),"xxxxx"));
    }

    List<JSONObject> getViewResponse(Cart cart, SimpleDateFormat sdf){
        List<JSONObject> entities = new ArrayList<JSONObject>();
        JSONObject entity = new JSONObject();
        entity.put("Property", cart.getProperty().get(0));
        String inDate = sdf.format(cart.getCheckinDate().get(0));
        entity.put("Checkin date", inDate);
        String outDate = sdf.format(cart.getCheckoutDate().get(0));
        entity.put("Checkout date", outDate);
        entities.add(entity);
        JSONObject price = new JSONObject();
        price.put("Cart value", cart.getCartValue());
        entities.add(price);
        return entities;
    }
    RentalProperty getProperty(RentalProperty property){
        property.setPrice_per_night(70f);
        property.setNum_bedrooms(3);
        property.setAvailability(1);
        property.setNum_baths(2);
        property.setProperty_description("Lakeside villa");
        property.setProperty_name("Marquis");
        property.setProperty_id(1);
        property.setProperty_type("Villa");
        property.setCity("Madison");
        property.setPet_friendly(1);
        property.setWifi_avail(1);
        property.setCarpet_area(1900);
        return property;
    }


    @Test
    @DisplayName("Invalid user remove from cart")
    void invalidUserDeleteCart(){
        Cart cart = getEmptyCart();
        Customer customer = getCustomer();
        customer.setCart(cart);
        CartRequest cr = getCartRequest();
        when(c.getDb().getCustomer(customer.getUsername())).thenReturn(null);
        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () -> c.removeFromCart(cr, "xxxxx"));
        assertEquals("Invalid user", exception.getMessage());
    }
    @Test
    @DisplayName("Invalid session remove from cart")
    void invalidSessionDeleteCart(){
        Cart cart = getEmptyCart();
        Customer customer = getCustomer();
        customer.setApiKey(null);
        customer.setCart(cart);
        when(c.getDb().getCustomer(customer.getUsername())).thenReturn(customer);
        UnauthorizedException exception = assertThrows(UnauthorizedException.class, () -> c.removeFromCart(getCartRequest(), "xxxxx"));
        assertEquals("Please login", exception.getMessage());
    }
//    @Test
//    @DisplayName("Invalid user remove from cart")
//    void invalidUserDeleteCart(){
//        Cart cart = getEmptyCart();
//        Customer customer = getCustomer();
//        customer.setCart(cart);
//        CartRequest cr = getCartRequest();
//        when(c.getDb().getCustomer(customer.getUsername())).thenReturn(null);
//        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> c.removeFromCart(cr, "xxxxx"));
//        assertEquals("Invalid user", exception.getMessage());
//    }
//    @Test
//    @DisplayName("Invalid session remove from cart")
//    void invalidSessionDeleteCart(){
//        Cart cart = getEmptyCart();
//        Customer customer = getCustomer();
//        customer.setApiKey(null);
//        customer.setCart(cart);
//        when(c.getDb().getCustomer(customer.getUsername())).thenReturn(customer);
//        UnauthorizedException exception = assertThrows(UnauthorizedException.class, () -> c.removeFromCart(getCartRequest(), "xxxxx"));
//        assertEquals("Please login", exception.getMessage());
//    }

    CartRequest getCartRequest(){
        CartRequest cr = new CartRequest();
        cr.setUsername("cherry012");
        cr.setPropertyID(1);
        cr.setCheckinDate("12-12-2022");
        cr.setCheckoutDate("12-14-2022");
        return cr;
    }
//    @Test
//    @DisplayName("Invalid API key remove from cart")
//    void invalidAPIKeyDeleteCart(){
//        Cart cart = getEmptyCart();
//        Customer customer = getCustomer();
//        customer.setCart(cart);
//        when(c.getDb().getCustomer(customer.getUsername())).thenReturn(customer);
//        UnauthorizedException exception = assertThrows(UnauthorizedException.class, () -> c.removeFromCart(getCartRequest(), "yyyyy"));
//        assertEquals("Unauthenticated - incorrect API Key.", exception.getMessage());
//    }

    @Test
    @DisplayName("Remove from cart when property does not exist/ empty cart")
    void invalidPropertyDeleteCart(){
        Cart cart = getEmptyCart();
        Customer customer = getCustomer();
        customer.setCart(cart);
        when(c.getDb().getCustomer(customer.getUsername())).thenReturn(customer);
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> c.removeFromCart(getCartRequest(), "xxxxx"));
        assertEquals("Property does not exist in cart", exception.getMessage());
    }
    @Test
    @DisplayName("Remove from cart successfully")
    void successfulDeleteCart(){
        Cart cart = getEmptyCart();
        Customer customer = getCustomer();
        RentalProperty property = new Villa();
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
        try{
            cart.addToCart(getProperty(property), sdf.parse("12-12-2022"), sdf.parse("12-14-2022"));
        }
        catch(Exception e){
            System.out.println(e);
        }
        customer.setCart(cart);
        when(c.getDb().getCustomer(customer.getUsername())).thenReturn(customer);
        when(c.getDb().updateCart(customer)).thenReturn(1);
        assertEquals("Removed from cart successfully", c.removeFromCart(getCartRequest(), "xxxxx"));
    }
    HostPropertyRequest hostPropertyRequest(){
        HostPropertyRequest hp = new HostPropertyRequest();
        hp.setAvailability(1);
        hp.setCity("Dallas");
        hp.setProperty_description("This is a lovely home.");
        hp.setProperty_type("Villa");
        hp.setProperty_name("SpringField");
        hp.setCarpet_area(1234);
        hp.setWifi_avail(1);
        hp.setPet_friendly(0);
        hp.setNum_of_bathrooms(2);
        hp.setNum_of_bedrooms(2);
        hp.setPrice_per_night(35.0f);
        return hp;
    }

    HostPropertyRequest invalidHostPropertyRequest(){
        HostPropertyRequest hp = new HostPropertyRequest();
        hp.setAvailability(1);
        hp.setCity("Dallas");
        hp.setProperty_description("This is a lovely home.");
        hp.setProperty_type("cottage");
        hp.setProperty_name("SpringField");
        hp.setCarpet_area(1234);
        hp.setWifi_avail(1);
        hp.setPet_friendly(0);
        hp.setNum_of_bathrooms(2);
        hp.setNum_of_bedrooms(2);
        hp.setPrice_per_night(35.0f);
        return hp;
    }

    @Test
    @DisplayName("Host a Villa")
    void hostPropertySuccessfully(){
        Customer user = getCustomer();
        RentalProperty property = new Villa();
        PropertyFactory factory = new FirstClassFactory();


        when(c.getDb().getCustomer(user.getUsername())).thenReturn(user);
//        when(c.getProducer().getFactory("FirstClass")).thenReturn(factory);
//        when(c.getFactory().getProperty("villa")).thenReturn(property);
//        PropertyFactory factory1 = Mockito.spy(factory);
//        Mockito.doReturn(property).when(factory1).getProperty("villa");
        when(c.getDb().getsetProperty("villa")).thenReturn(property);
        when(c.getDb().hostProperty(property)).thenReturn(1);
        assertEquals("Hosted property successfully.", c.hostProperty(hostPropertyRequest(), user.getUsername(), user.getApiKey()));
    }

    @Test
    @DisplayName("Invalid User for hosting property")
    void hostPropertyInvalidUser() {
        Customer user = getCustomer();
        when(c.getDb().getCustomer(user.getUsername())).thenReturn(null);
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> c.hostProperty(hostPropertyRequest(), user.getUsername(), user.getApiKey()));
        assertEquals("Invalid user.", exception.getMessage());
    }

    @Test
    @DisplayName("Missing apikey for hosting property")
    void hostPropertyMissingAPIKey() {
        Customer user = getCustomer();
        user.setApiKey(null);
        when(c.getDb().getCustomer(user.getUsername())).thenReturn(user);
        UnauthorizedException exception = assertThrows(UnauthorizedException.class, () -> c.hostProperty(hostPropertyRequest(), user.getUsername(), null));
        assertEquals("Please login.", exception.getMessage());
    }

    @Test
    @DisplayName("Invalid apikey for hosting property")
    void hostPropertyInvalidAPIKey() {
        Customer user = getCustomer();
        user.setApiKey("abcd");
        when(c.getDb().getCustomer(user.getUsername())).thenReturn(user);
        UnauthorizedException exception = assertThrows(UnauthorizedException.class, () -> c.hostProperty(hostPropertyRequest(), user.getUsername(), "xyz"));
        assertEquals("Unauthenticated - incorrect API Key.", exception.getMessage());
    }

    @Test
    @DisplayName("Could not host property")
    void hostPropertyUnsuccessful(){
        Customer user = getCustomer();
        RentalProperty property = new Villa();
        when(c.getDb().getCustomer(user.getUsername())).thenReturn(user);
        when(c.getDb().getsetProperty("villa")).thenReturn(property);
        when(c.getDb().hostProperty(property)).thenReturn(0);
        RuntimeException exception = assertThrows(RuntimeException.class, () -> c.hostProperty(hostPropertyRequest(), user.getUsername(), user.getApiKey()));
        assertEquals("Could not host property.", exception.getMessage());
    }

    @Test
    @DisplayName("Invalid request for host property")
    void hostPropertyInvalidRequest(){
        Customer user = getCustomer();
        RentalProperty property = new Villa();
        PropertyFactory factory = new FirstClassFactory();
        when(c.getDb().getCustomer(user.getUsername())).thenReturn(user);
        PropertyFactory factory1 = Mockito.spy(factory);
        Mockito.doReturn(property).when(factory1).getProperty("villa");
        when(c.getDb().hostProperty(property)).thenReturn(0);
        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () -> c.hostProperty(invalidHostPropertyRequest(), user.getUsername(), user.getApiKey()));
        assertEquals("Property type should belong to (Villa, BeachHouse, Resort, Apartment, Studio, House, Motel).", exception.getMessage());
    }
    RatePropertyRequest ratePropertyRequest(){
        RatePropertyRequest rp = new RatePropertyRequest();
        rp.setRating(4.5);
        rp.setPropertyID(4);
        rp.setReservationID(12);
        return rp;
    }
    @Test
    @DisplayName("Invalid User for rate property")
    void ratePropertyInvalidUser() {
        Customer user = getCustomer();
        when(c.getDb().getCustomer(user.getUsername())).thenReturn(null);
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> c.rateProperty(ratePropertyRequest(), user.getUsername(), user.getApiKey()));
        assertEquals("Invalid user.", exception.getMessage());
    }

    @Test
    @DisplayName("Missing apikey for rate property")
    void ratePropertyMissingAPIKey() {
        Customer user = getCustomer();
        user.setApiKey(null);
        when(c.getDb().getCustomer(user.getUsername())).thenReturn(user);
        UnauthorizedException exception = assertThrows(UnauthorizedException.class, () -> c.rateProperty(ratePropertyRequest(), user.getUsername(), null));
        assertEquals("Please login.", exception.getMessage());
    }

    @Test
    @DisplayName("Invalid apikey for rate property")
    void ratePropertyInvalidAPIKey() {
        Customer user = getCustomer();
        user.setApiKey("abcd");
        when(c.getDb().getCustomer(user.getUsername())).thenReturn(user);
        UnauthorizedException exception = assertThrows(UnauthorizedException.class, () -> c.rateProperty(ratePropertyRequest(), user.getUsername(), "xyz"));
        assertEquals("Unauthenticated - incorrect API Key.", exception.getMessage());
    }

    ArrayList<Reservation> getReservations() throws ParseException {
        ArrayList<Reservation> reservations = new ArrayList<>();
        Reservation r = new Reservation();
        RentalProperty p = new Villa();
        Customer user = getCustomer();
        p = getProperty(p);
        r.setProperty(p);
        r.setCustomer(user);
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
        r.setCheckinDate(sdf.parse("11-12-2022"));
        r.setCheckoutDate(sdf.parse("11-14-2022"));
        r.setConfirmationNumber(555);
        reservations.add(r);
        return reservations;
    }
    ArrayList<Reservation> getInvalidUserReservation() throws ParseException {
        ArrayList<Reservation> reservations = new ArrayList<>();
        Reservation r = new Reservation();
        RentalProperty p = new Villa();
        Customer user = getCustomer();
        user.setUsername("Tom");
        p = getProperty(p);
        r.setProperty(p);
        r.setCustomer(user);
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
        r.setCheckinDate(sdf.parse("12-12-2022"));
        r.setCheckoutDate(sdf.parse("12-14-2022"));
        r.setConfirmationNumber(555);
        reservations.add(r);
        return reservations;
    }
    @Test
    @DisplayName("Invalid reservation for rate property")
    void ratePropertyInvalidReservationID() throws ParseException {
        Customer user = getCustomer();
        ArrayList<Reservation> reservations = getReservations();
        when(c.getDb().getCustomer(user.getUsername())).thenReturn(user);
        when(c.getDb().getReservations(ratePropertyRequest().getReservationID())).thenReturn(null);
        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () -> c.rateProperty(ratePropertyRequest(), user.getUsername(), user.getApiKey()));
        assertEquals("Invalid Reservation ID", exception.getMessage());
    }

    @Test
    @DisplayName("Invalid reservation for user for rate property")
    void ratePropertyInvalidUserReservationID() throws ParseException {
        Customer user = getCustomer();
        ArrayList<Reservation> reservations = getInvalidUserReservation();
        when(c.getDb().getCustomer(user.getUsername())).thenReturn(user);
        when(c.getDb().getReservations(ratePropertyRequest().getReservationID())).thenReturn(reservations);
        UnauthorizedException exception = assertThrows(UnauthorizedException.class, () -> c.rateProperty(ratePropertyRequest(), user.getUsername(), user.getApiKey()));
        assertEquals("Reservation does not belong to user!", exception.getMessage());
    }
    ArrayList<Reservation> getInvalidDateReservation() throws ParseException {
        ArrayList<Reservation> reservations = new ArrayList<>();
        Reservation r = new Reservation();
        RentalProperty p = new Villa();
        Customer user = getCustomer();
        p = getProperty(p);
        r.setProperty(p);
        r.setCustomer(user);
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
        r.setCheckinDate(sdf.parse("12-12-2022"));
        r.setCheckoutDate(sdf.parse("12-14-2022"));
        r.setConfirmationNumber(555);
        reservations.add(r);
        return reservations;
    }

    RatePropertyRequest ratePropertyValidRequest(){
        RatePropertyRequest rp = new RatePropertyRequest();
        rp.setRating(4.5);
        rp.setPropertyID(1);
        rp.setReservationID(12);
        return rp;
    }
    RentalProperty getPropertyVilla(RentalProperty property){
        property.setPrice_per_night(70f);
        property.setNum_bedrooms(3);
        property.setAvailability(1);
        property.setNum_baths(2);
        property.setProperty_description("This is a beautiful house.");
        property.setProperty_name("HighlandSprings");
        property.setProperty_id(4);
        property.setProperty_type("Villa");
        property.setCity("Austin");
        property.setPet_friendly(1);
        property.setWifi_avail(1);
        property.setCarpet_area(1500);
        property.setAverage_rating(4.5);
        return property;
    }
    @Test
    @DisplayName("Invalid property for rate property")
    void ratePropertyInvalidProperty() throws ParseException {
        Customer user = getCustomer();
        ArrayList<Reservation> reservations = getInvalidDateReservation();
        RentalProperty property = getPropertyVilla(new Villa());
        when(c.getDb().getCustomer(user.getUsername())).thenReturn(user);
        when(c.getDb().getReservations(ratePropertyRequest().getReservationID())).thenReturn(reservations);
        when(c.getDb().getProperty(ratePropertyRequest().getPropertyID())).thenReturn(property);
        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () -> c.rateProperty(ratePropertyRequest(), user.getUsername(), user.getApiKey()));
        assertEquals("Property ID does not belong to reservation.", exception.getMessage());
    }

    @Test
    @DisplayName("Invalid date for rate property")
    void ratePropertyInvalidDate() throws ParseException {
        Customer user = getCustomer();
        ArrayList<Reservation> reservations = getInvalidDateReservation();
        RentalProperty property = getProperty(new Villa());
        when(c.getDb().getCustomer(user.getUsername())).thenReturn(user);
        when(c.getDb().getReservations(ratePropertyValidRequest().getReservationID())).thenReturn(reservations);
        when(c.getDb().getProperty(ratePropertyValidRequest().getPropertyID())).thenReturn(property);
        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () -> c.rateProperty(ratePropertyValidRequest(), user.getUsername(), user.getApiKey()));
        assertEquals("Too soon to rate property. Wait until check-out date", exception.getMessage());
    }

    @Test
    @DisplayName("Successfully rate property")
    void ratePropertySuccess() throws ParseException {
        Customer user = getCustomer();
        ArrayList<Reservation> reservations = getReservations();
        RentalProperty property = getProperty(new Villa());
        property.setAverage_rating(4.5);
        property.setNumber_of_reviews(10);
        when(c.getDb().getCustomer(user.getUsername())).thenReturn(user);
        when(c.getDb().getReservations(ratePropertyValidRequest().getReservationID())).thenReturn(reservations);
        when(c.getDb().getProperty(ratePropertyValidRequest().getPropertyID())).thenReturn(property);
        when(c.getDb().saveRating(ratePropertyValidRequest().getPropertyID(), 4.5, property.getNumber_of_reviews() + 1)).thenReturn(1);
        assertEquals("Thank you for your review!", c.rateProperty(ratePropertyValidRequest(), user.getUsername(), user.getApiKey()));
    }

    @Test
    @DisplayName("Could not rate property")
    void ratePropertyNotSuccessful() throws ParseException {
        Customer user = getCustomer();
        ArrayList<Reservation> reservations = getReservations();
        RentalProperty property = getPropertyVilla(new Villa());
        property.setProperty_id(1);
        property.setNumber_of_reviews(10);
        when(c.getDb().getCustomer(user.getUsername())).thenReturn(user);
        when(c.getDb().getReservations(ratePropertyValidRequest().getReservationID())).thenReturn(reservations);
        when(c.getDb().getProperty(ratePropertyValidRequest().getPropertyID())).thenReturn(property);
        when(c.getDb().saveRating(ratePropertyValidRequest().getPropertyID(), 4.5,property.getNumber_of_reviews()+1 )).thenReturn(0);
        RuntimeException exception = assertThrows(RuntimeException.class, () -> c.rateProperty(ratePropertyValidRequest(), user.getUsername(), user.getApiKey()));
        assertEquals("Could not rate property.", exception.getMessage());
    }
    @Test
    @DisplayName("Generating invoice with no coupons")
    void generateInvoiceWithNoCoupons() throws ParseException {
        Customer customer = new Customer();
        customer.setUsername("cherry012");
        customer.setPassword("cher123");
        customer.setApiKey("xxxxx");
        customer.setUserID(1);
        Cart cart = new Cart();
        cart.setCartID(2);
        cart.setCartValue(50f);
        RentalProperty property1 = new House();
        property1.setPrice_per_night(50f);
        ArrayList<RentalProperty> properties = new ArrayList<>();
        properties.add(property1);
        cart.setProperty(properties);
        customer.setCart(cart);
        when(c.getDb().getCustomer(customer.getUsername())).thenReturn(customer);
        assertEquals(56, (int) c.generateInvoice(null, "cherry012", "xxxxx"));

    }

    @Test
    @DisplayName("Generating invoice with valid coupons")
    void generateInvoiceWithValidCoupons() throws ParseException {
        Customer customer = new Customer();
        customer.setUsername("cherry012");
        customer.setPassword("cher123");
        customer.setApiKey("xxxxx");
        customer.setUserID(1);
        Cart cart = new Cart();
        cart.setCartID(2);
        cart.setCartValue(50f);
        RentalProperty property1 = new House();
        property1.setPrice_per_night(50f);
        ArrayList<RentalProperty> properties = new ArrayList<>();
        properties.add(property1);
        cart.setProperty(properties);
        customer.setCart(cart);
        CouponList list = new CouponList();
        Coupon c1 = new Coupon();
        c1.setCouponCode("c1");
        Coupon c2 = new Coupon();
        c2.setCouponCode("c2");
        List<Coupon> coupons = new ArrayList<>();
        coupons.add(c1);
        coupons.add(c2);
        list.setCoupons(coupons);
        when(c.getDb().getCustomer(customer.getUsername())).thenReturn(customer);
        assertEquals(47, (int) c.generateInvoice(list, "cherry012", "xxxxx"));

    }

    @Test
    @DisplayName("Generating invoice with invalid coupons")
    void generateInvoiceWithInvalidCoupons() throws ParseException {
        Customer customer = new Customer();
        customer.setUsername("cherry012");
        customer.setPassword("cher123");
        customer.setApiKey("xxxxx");
        customer.setUserID(1);
        Cart cart = new Cart();
        cart.setCartID(2);
        cart.setCartValue(50f);
        RentalProperty property1 = new House();
        property1.setPrice_per_night(50f);
        ArrayList<RentalProperty> properties = new ArrayList<>();
        properties.add(property1);
        cart.setProperty(properties);
        customer.setCart(cart);
        CouponList list = new CouponList();
        Coupon c1 = new Coupon();
        c1.setCouponCode("c23232");
        Coupon c2 = new Coupon();
        c2.setCouponCode("c2");
        List<Coupon> coupons = new ArrayList<>();
        coupons.add(c1);
        coupons.add(c2);
        list.setCoupons(coupons);
        when(c.getDb().getCustomer(customer.getUsername())).thenReturn(customer);
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> c.generateInvoice(list, "cherry012", "xxxxx"));
        assertEquals("Coupon not found", exception.getMessage());

    }

    @Test
    @DisplayName("Generating invoice with invalid coupons")
    void generateInvoiceWithRepeatedCoupons() throws ParseException {
        Customer customer = new Customer();
        customer.setUsername("cherry012");
        customer.setPassword("cher123");
        customer.setApiKey("xxxxx");
        customer.setUserID(1);
        Cart cart = new Cart();
        cart.setCartID(2);
        cart.setCartValue(50f);
        RentalProperty property1 = new House();
        property1.setPrice_per_night(50f);
        ArrayList<RentalProperty> properties = new ArrayList<>();
        properties.add(property1);
        cart.setProperty(properties);
        customer.setCart(cart);
        CouponList list = new CouponList();
        Coupon c1 = new Coupon();
        c1.setCouponCode("c2");
        Coupon c2 = new Coupon();
        c2.setCouponCode("c2");
        List<Coupon> coupons = new ArrayList<>();
        coupons.add(c1);
        coupons.add(c2);
        list.setCoupons(coupons);
        when(c.getDb().getCustomer(customer.getUsername())).thenReturn(customer);
        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () -> c.generateInvoice(list, "cherry012", "xxxxx"));
        assertEquals("Coupon c2 already added", exception.getMessage());

    }
    @Test
    @DisplayName("Generating invoice with invalid coupons")
    void generateInvoiceWithNothingInCart() throws ParseException {
        Customer customer = new Customer();
        customer.setUsername("cherry012");
        customer.setPassword("cher123");
        customer.setApiKey("xxxxx");
        customer.setUserID(1);
        Cart cart = new Cart();
        cart.setCartID(2);
        cart.setCartValue(0f);
        customer.setCart(cart);
        when(c.getDb().getCustomer(customer.getUsername())).thenReturn(customer);
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> c.generateInvoice(null, "cherry012", "xxxxx"));
        assertEquals("User does not have any properties in cart", exception.getMessage());

    }

    @Test
    @DisplayName("Generating invoice as unauthorized user")
    void generateInvoiceAsUnauthorizedUser() throws ParseException {
        Customer customer = new Customer();
        customer.setUsername("cherry012");
        customer.setPassword("cher123");
        customer.setApiKey("xxxxx");
        customer.setUserID(1);
        Cart cart = new Cart();
        cart.setCartID(2);
        cart.setCartValue(0f);
        customer.setCart(cart);
        when(c.getDb().getCustomer(customer.getUsername())).thenReturn(customer);
        UnauthorizedException exception = assertThrows(UnauthorizedException.class, () -> c.generateInvoice(null, "dfsf", "xxxxx"));
        assertEquals("Unauthorized or Invalid user", exception.getMessage());

    }

    @Test
    @DisplayName("Generating invoice with invalid API Key")
    void generateInvoiceWithInvalidAPIKey() throws ParseException {
        Customer customer = new Customer();
        customer.setUsername("cherry012");
        customer.setPassword("cher123");
        customer.setApiKey("xxxxx");
        customer.setUserID(1);
        Cart cart = new Cart();
        cart.setCartID(2);
        cart.setCartValue(0f);
        customer.setCart(cart);
        when(c.getDb().getCustomer(customer.getUsername())).thenReturn(customer);
        UnauthorizedException exception = assertThrows(UnauthorizedException.class, () -> c.generateInvoice(null, "dfsf", "yyyyyy"));
        assertEquals("Unauthorized or Invalid user", exception.getMessage());

    }


}
