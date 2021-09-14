package com.epam.esm.dao.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
public class PersistenceJPAConfig {
    public static void main(String[] args) {
        ConfigurableApplicationContext run = SpringApplication.run(PersistenceJPAConfig.class, args);
        System.out.println("lol");
    }
}
