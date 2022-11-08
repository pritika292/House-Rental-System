package com.reservation.rentaplace.Controller;
import com.reservation.rentaplace.Domain.Customer;
import com.reservation.rentaplace.Domain.CustomerRequest;
import com.reservation.rentaplace.DAO.DBMgr;
import com.reservation.rentaplace.Domain.Property;
import com.reservation.rentaplace.Domain.Login;
import com.reservation.rentaplace.Domain.Filter;
import com.reservation.rentaplace.Service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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
