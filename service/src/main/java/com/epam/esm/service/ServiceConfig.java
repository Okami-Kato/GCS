package com.epam.esm.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class ServiceConfig {
    public static void main(String[] args) {
        ConfigurableApplicationContext run = SpringApplication.run(ServiceConfig.class, args);
        System.out.println("fds");
    }
}
