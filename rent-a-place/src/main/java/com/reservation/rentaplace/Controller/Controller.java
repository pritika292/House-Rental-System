package com.reservation.rentaplace.Controller;
import com.reservation.rentaplace.Domain.Customer;
import com.reservation.rentaplace.Domain.CustomerRequest;
import com.reservation.rentaplace.DAO.DBMgr;
import com.reservation.rentaplace.Domain.Property;
import com.reservation.rentaplace.Domain.Login;
import com.reservation.rentaplace.Domain.Filter;
import com.reservation.rentaplace.Domain.Reservation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@RestController
public class Controller
{
    @Autowired
    private DBMgr db;
    @PostMapping("/register")
    public String save(@RequestBody CustomerRequest c) {
        int cartId = db.createCart();
        return db.save(c, cartId)+" Customer registered successfully";
    }
    @PostMapping("/login")
    public String login(@RequestBody Login l) {
        return null;
    }
    @GetMapping("/view/{location}/{dates}")
    public Property getProperty(@PathVariable String location , @PathVariable String[] dates) {
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

}
