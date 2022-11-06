package com.reservation.rentaplace.Service;

import com.reservation.rentaplace.DAO.DBMgr;
import com.reservation.rentaplace.Domain.Customer;
import com.reservation.rentaplace.Domain.Login;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CustomerService
{
    @Autowired
    private DBMgr db;
    public CustomerService(DBMgr db)
    {
        this.db = db;
    }
    public String verifyLogin(String username, String password)
    {
        Customer c = db.getCustomer(username);
        if (c != null) {
            if (c.getPassword().equals(password)) {
                return "Login successful";
            }
        }
        return "Login unsuccessful";
    }
}
