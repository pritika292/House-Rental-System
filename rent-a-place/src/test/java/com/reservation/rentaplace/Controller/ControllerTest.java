package com.reservation.rentaplace.Controller;

import com.reservation.rentaplace.DAO.DBMgr;
import com.reservation.rentaplace.Domain.*;
import com.reservation.rentaplace.Domain.Request.CustomerRequest;
import com.reservation.rentaplace.Exception.InvalidRequestException;
import com.reservation.rentaplace.Exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import com.reservation.rentaplace.Exception.UnauthorizedException;
import com.reservation.rentaplace.RentAPlaceApplication;
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
import java.util.Arrays;
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

    @Mock
    SearchPropertyRequest searchPropertyRequest;

    @Mock
    Apartment rentalProperty;

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
    @DisplayName("City value is null")
    void invalidCityForViewProperty() throws ParseException {
        Reservation reservation = Mockito.mock(Reservation.class);
        Customer customer = Mockito.mock(Customer.class);
        List<RentalProperty> list = Arrays.asList(rentalProperty);
        SearchPropertyRequest searchPropertyRequest = new SearchPropertyRequest();
        Mockito.when(searchPropertyRequest.getCity()).thenReturn("NewYork");
        Mockito.when(searchPropertyRequest.getCheckIn()).thenReturn("11-29-2022");
        Mockito.when(searchPropertyRequest.getCheckOut()).thenReturn("11-29-2022");
        //Mockito.when(db.getProperties(searchPropertyRequest)).thenReturn();
        //Mockito.when(db.getReservations()).thenReturn(Arrays.asList(reservation));
        //Mockito.when(rentalProperty.getProperty_id()).thenReturn(123);
        Mockito.when(reservation.getProperty()).thenReturn(rentalProperty);
        Mockito.when(reservation.getCheckinDate()).thenReturn(new Date("11-29-2022"));
        Mockito.when(reservation.getCheckoutDate()).thenReturn(new Date("11-29-2022"));
        Mockito.when(customer.getName()).thenReturn("name");
        Mockito.when(customer.getPhone_number()).thenReturn("12345678");
        Mockito.when(customer.getEmail()).thenReturn("abc@abc.com");
        Mockito.when(db.getCustomerByID(123)).thenReturn(customer);
        //Mockito.when(searchPropertyRequest.getAverage_rating()).thenReturn(new Float(4.0));
        Mockito.when(searchPropertyRequest.getCarpet_area()).thenReturn(12);
        Mockito.when(searchPropertyRequest.getNum_baths()).thenReturn(23);
        Mockito.when(searchPropertyRequest.getNum_bedrooms()).thenReturn(2);
        Mockito.when(searchPropertyRequest.getPet_friendly()).thenReturn(0);
        //Mockito.when(searchPropertyRequest.getPrice_per_night()).thenReturn(new Float("1500"));
        Mockito.when(searchPropertyRequest.getWifi_avail()).thenReturn(1);
        when(c.getProperty(searchPropertyRequest)).thenReturn(searchPropertyRequest);

        assertEquals(list, c.getProperty(searchPropertyRequest));


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
}
