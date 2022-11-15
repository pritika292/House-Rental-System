package com.reservation.rentaplace.Domain;
import java.util.HashMap;
import java.util.Map;
public class Constants {
    private static HashMap<String, String> propertyClass = new HashMap<>();

    public static HashMap<String, String> getPropertyClass(){
        propertyClass.put("villa", "FirstClass");
        propertyClass.put("beachHouse", "FirstClass");
        propertyClass.put("resort", "FirstClass");
        propertyClass.put("apartment", "BusinessClass");
        propertyClass.put("house", "BusinessClass");
        propertyClass.put("studio", "BusinessClass");
        propertyClass.put("motel", "EconomyClass");
        return propertyClass;
    }

}
