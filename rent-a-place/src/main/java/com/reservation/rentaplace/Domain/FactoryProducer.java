package com.reservation.rentaplace.Domain;

import org.springframework.context.annotation.Bean;


public class FactoryProducer {
    public PropertyFactory getFactory(String factoryType){
        if(factoryType.equalsIgnoreCase("FirstClass")){
            return new FirstClassFactory();
        } else if (factoryType.equalsIgnoreCase("BusinessClass")) {
            return new BusinessClassFactory();
        }
        else{
            return new EconomyClassFactory();
        }
    }
}
