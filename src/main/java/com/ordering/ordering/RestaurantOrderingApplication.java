package com.ordering.ordering;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.ordering"})
public class RestaurantOrderingApplication {

    public static void main(String[] args) {
        SpringApplication.run(RestaurantOrderingApplication.class, args);
    }

}
