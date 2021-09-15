package com.epam.esm.dao.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@ComponentScan("com.epam.esm.dao")
@EntityScan("com.epam.esm.entity")
@EnableTransactionManagement
public class DaoConfig {
    public static void main(String[] args) {
        ConfigurableApplicationContext run = SpringApplication.run(DaoConfig.class, args);
        System.out.println("lol");
    }
}
