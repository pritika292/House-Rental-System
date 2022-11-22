package com.reservation.rentaplace.Domain.Command;

import com.reservation.rentaplace.Domain.Cart;

import java.util.ArrayList;
import java.util.List;

public class InvoiceGenerator
{

    private static InvoiceGenerator generator;
    private List<AddOn> addOns;


    public static InvoiceGenerator getInvoiceGenerator()
    {
        if (generator == null)
        {
            generator = new InvoiceGenerator();
        }
        return generator;

    }

    private InvoiceGenerator()
    {
        addOns = new ArrayList<>();
    }

    public void addTransaction(AddOn a)
    {
        addOns.add(a);
    }

    public float generateInvoice(List<Float> coupons, float price)
    {
        if (coupons != null)
        {
            for (Float f: coupons)
            {
                AddOn a = new AddCoupon(f);
                this.addTransaction(a);
            }
        }
        this.addTransaction(new AddTax());
        this.addTransaction(new AddProcessingFee());
        for (int i = 0; i < addOns.size(); i++)
        {
            price = addOns.get(i).executeTransaction(price);
            System.out.println(price);
        }
        addOns = new ArrayList<>();
        return price;
    }
}
