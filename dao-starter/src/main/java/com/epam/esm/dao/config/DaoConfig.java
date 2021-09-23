package com.epam.esm.dao.config;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootConfiguration
@ComponentScan("com.epam.esm.dao")
@EntityScan("com.epam.esm.entity")
@EnableAutoConfiguration
@EnableTransactionManagement
public class DaoConfig {

}
