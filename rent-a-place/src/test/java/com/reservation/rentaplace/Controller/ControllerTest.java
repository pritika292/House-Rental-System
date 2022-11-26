package com.reservation.rentaplace.Controller;

import com.nimbusds.jose.shaded.json.JSONObject;
import com.reservation.rentaplace.DAO.DBMgr;
import com.reservation.rentaplace.Domain.*;
import com.reservation.rentaplace.Domain.Request.CartRequest;
import com.reservation.rentaplace.Domain.Request.CustomerRequest;
import com.reservation.rentaplace.Domain.Request.HostPropertyRequest;
import com.reservation.rentaplace.Exception.InvalidRequestException;
import com.reservation.rentaplace.Exception.ResourceNotFoundException;
import com.reservation.rentaplace.Exception.UnauthorizedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
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
        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () -> c.save(cr));
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
        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () -> c.save(cr));
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
        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () -> c.save(cr));
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
        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () -> c.save(cr));
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
        assertEquals(cr.getName()+ " registered successfully.", c.save(cr));
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
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> c.addToCart(new CartRequest(), "xxxxx"));
        assertEquals("Invalid user", exception.getMessage());
    }

    @Test
    @DisplayName("Invalid session for add to cart")
    void invalidSessionAddToCart(){
        Customer customer = new Customer();
        customer.setUsername("cherry012");
        customer.setPassword("cher123");
        customer.setApiKey(null);
        CartRequest cr = new CartRequest();
        cr.setUsername("garry123");
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
        Customer customer = new Customer();
        customer.setUsername("cherry012");
        customer.setPassword("cher123");
        customer.setApiKey("yyyyy");
        CartRequest cr = new CartRequest();
        cr.setUsername("garry123");
        cr.setPropertyID(1);
        cr.setCheckinDate("12-12-2022");
        cr.setCheckoutDate("14-12-2022");
        when(c.getDb().getCustomer(cr.getUsername())).thenReturn(customer);
        UnauthorizedException exception = assertThrows(UnauthorizedException.class, () -> c.addToCart(cr, "xxxxx"));
        assertEquals("Unauthenticated - incorrect API Key.", exception.getMessage());
    }

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
        cr.setUsername("garry123");
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
        cr.setUsername("garry123");
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
        cr.setUsername("garry123");
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
        cr.setUsername("garry123");
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
        cr.setUsername("garry123");
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
        cr.setUsername("garry123");
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
        cr.setUsername("garry123");
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

    @Test
    @DisplayName("Invalid user view cart")
    void invalidUserViewCart(){
        Cart cart = getEmptyCart();
        Customer customer = getCustomer();
        customer.setCart(cart);
        when(c.getDb().getCustomer(customer.getUsername())).thenReturn(null);
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> c.viewCart(customer.getUsername(), "xxxxx"));
        assertEquals("Invalid User", exception.getMessage());
    }
    @Test
    @DisplayName("Invalid session view cart")
    void invalidSessionViewCart(){
        Cart cart = getEmptyCart();
        Customer customer = getCustomer();
        customer.setApiKey(null);
        customer.setCart(cart);
        when(c.getDb().getCustomer(customer.getUsername())).thenReturn(customer);
        UnauthorizedException exception = assertThrows(UnauthorizedException.class, () -> c.viewCart(customer.getUsername(), "yyyyy"));
        assertEquals("Please login", exception.getMessage());
    }

    @Test
    @DisplayName("Invalid API key view cart")
    void invalidAPIKeyViewCart(){
        Cart cart = getEmptyCart();
        Customer customer = getCustomer();
        customer.setCart(cart);
        when(c.getDb().getCustomer(customer.getUsername())).thenReturn(customer);
        UnauthorizedException exception = assertThrows(UnauthorizedException.class, () -> c.viewCart(customer.getUsername(), "yyyyy"));
        assertEquals("Unauthenticated - incorrect API Key.", exception.getMessage());
    }
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
        property.setProperty_type("Villa");
        property.setCity("Madison");
        property.setPet_friendly(1);
        property.setWifi_avail(1);
        property.setCarpet_area(1900);
        return property;
    }
}
