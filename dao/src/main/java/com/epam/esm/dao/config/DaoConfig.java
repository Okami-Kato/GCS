package com.epam.esm.dao.config;

import com.epam.esm.dao.impl.CustomRepositoryImpl;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@ComponentScan("com.epam.esm.dao")
@EnableJpaRepositories(value = "com.epam.esm.dao", repositoryBaseClass = CustomRepositoryImpl.class)
@EntityScan("com.epam.esm.entity")
@EnableTransactionManagement
public class DaoConfig {
}
