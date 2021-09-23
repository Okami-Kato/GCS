package com.epam.esm.dao;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@ComponentScan
@EntityScan("com.epam.esm.entity")
@EnableTransactionManagement
public class DaoConfig {
}
