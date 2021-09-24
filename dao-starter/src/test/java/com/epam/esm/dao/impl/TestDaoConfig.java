package com.epam.esm.dao.impl;

import com.epam.esm.dao.DaoConfig;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Import;

@SpringBootConfiguration
@EnableAutoConfiguration
@Import(DaoConfig.class)
public class TestDaoConfig {
}
