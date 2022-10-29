package com.reservation.rentaplace.Domain;

import com.sun.org.apache.xpath.internal.operations.Bool;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Bean;

import java.util.Map;

public class Property
{
   @Getter
   @Setter
    Map<String[], Boolean> availability;
    @Setter
    Integer rating;

}
