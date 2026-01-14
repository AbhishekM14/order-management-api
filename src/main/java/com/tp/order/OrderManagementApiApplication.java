package com.tp.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class OrderManagementApiApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(OrderManagementApiApplication.class, args);
    }
}
