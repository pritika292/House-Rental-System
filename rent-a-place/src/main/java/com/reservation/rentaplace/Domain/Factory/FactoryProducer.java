package com.reservation.rentaplace.Domain.Factory;


public class FactoryProducer {

    private static FactoryProducer instance;

    public static FactoryProducer getInstance(){
        if(instance == null){
            instance = new FactoryProducer();
        }
        return instance;
    }
    public PropertyFactory getFactory(String factoryType){
        if(factoryType.equalsIgnoreCase("FirstClass")){
            return new FirstClassFactory();
        } else if (factoryType.equalsIgnoreCase("BusinessClass")) {
            return new BusinessClassFactory();
        }
        else if (factoryType.equalsIgnoreCase("EconomyClass")){
            return new EconomyClassFactory();
        }
        else return null;
    }
}
