package com.reservation.rentaplace.Domain;
import java.util.HashMap;
import java.util.Map;
public class Constants {
    private static HashMap<String, String> propertyClass = new HashMap<>();

    private static HashMap<String, Float> coupons = new HashMap<>();

    public static HashMap<String, String> getPropertyClass(){
        propertyClass.put("villa", "FirstClass");
        propertyClass.put("beach house", "FirstClass");
        propertyClass.put("resort", "FirstClass");
        propertyClass.put("apartment", "BusinessClass");
        propertyClass.put("house", "BusinessClass");
        propertyClass.put("studio", "BusinessClass");
        propertyClass.put("motel", "EconomyClass");
        return propertyClass;
    }

    public static HashMap<String, Float> getCoupons()
    {
        coupons.put("c1", 0.05f);
        coupons.put("c2", 0.10f);
        coupons.put("c3", 0.15f);
        coupons.put("c4", 0.08f);
        return coupons;
    }

}
