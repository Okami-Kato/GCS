package com.epam.esm;

import com.epam.esm.creator.CertificateCreator;
import com.epam.esm.creator.TagCreator;
import com.epam.esm.creator.UserCreator;
import com.epam.esm.creator.UserOrderCreator;
import com.epam.esm.properties.GenerationProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
@EnableConfigurationProperties(GenerationProperties.class)
public class GenerationApplication {
    public static void main(String[] args) {
        try (ConfigurableApplicationContext context = SpringApplication.run(GenerationApplication.class, args)) {
            GenerationProperties properties = context.getBean(GenerationProperties.class);

            TagCreator tagCreator = context.getBean(TagCreator.class);
            tagCreator.create(properties.getTagAmount());

            CertificateCreator certificateCreator = context.getBean(CertificateCreator.class);
            certificateCreator.create(properties.getCertificateAmount());

            UserCreator userCreator = context.getBean(UserCreator.class);
            userCreator.create(properties.getUserAmount());

            UserOrderCreator userOrderCreator = context.getBean(UserOrderCreator.class);
            userOrderCreator.create(properties.getUserOrderAmount());
        }
    }
}
