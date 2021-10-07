package com.epam.esm.data_generation;

import com.epam.esm.data_generation.creator.CertificateCreator;
import com.epam.esm.data_generation.creator.TagCreator;
import com.epam.esm.data_generation.creator.UserCreator;
import com.epam.esm.data_generation.creator.UserOrderCreator;
import com.epam.esm.data_generation.properties.CertificateProperties;
import com.epam.esm.data_generation.properties.TagProperties;
import com.epam.esm.data_generation.properties.UserOrderProperties;
import com.epam.esm.data_generation.properties.UserProperties;
import com.epam.esm.service.CertificateService;
import com.epam.esm.service.TagService;
import com.epam.esm.service.UserService;
import com.epam.esm.service.dto.response.CertificateItem;
import com.epam.esm.service.dto.response.TagResponse;
import com.epam.esm.service.dto.response.UserResponse;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.List;
import java.util.stream.Collectors;

@SpringBootApplication
@EnableConfigurationProperties({CertificateProperties.class, TagProperties.class, UserProperties.class, UserOrderProperties.class})
public class DataGenerationApplication {
    public static void main(String[] args) {
        try (ConfigurableApplicationContext context = SpringApplication.run(DataGenerationApplication.class, args)){
            TagProperties tagProperties = context.getBean(TagProperties.class);
            TagCreator tagCreator = context.getBean(TagCreator.class);
            tagCreator.create(tagProperties);

            TagService tagService = context.getBean(TagService.class);
            List<String> tagNames = tagService.getAll(1, Math.toIntExact(tagService.getCount()))
                    .stream()
                    .map(TagResponse::getName)
                    .collect(Collectors.toList());

            CertificateProperties certificateProperties = context.getBean(CertificateProperties.class);
            CertificateCreator certificateCreator = context.getBean(CertificateCreator.class);
            certificateCreator.create(certificateProperties, tagNames);

            UserProperties userProperties = context.getBean(UserProperties.class);
            UserCreator userCreator = context.getBean(UserCreator.class);
            userCreator.create(userProperties);

            CertificateService certificateService = context.getBean(CertificateService.class);
            UserService userService = context.getBean(UserService.class);
            List<Integer> certificateIds = certificateService.getAll(1, Math.toIntExact(certificateService.getCount()))
                    .stream()
                    .map(CertificateItem::getId)
                    .collect(Collectors.toList());
            List<Integer> userIds = userService.getAll(1, Math.toIntExact(userService.getCount()))
                    .stream()
                    .map(UserResponse::getId)
                    .collect(Collectors.toList());

            UserOrderProperties userOrderProperties = context.getBean(UserOrderProperties.class);
            UserOrderCreator userOrderCreator = context.getBean(UserOrderCreator.class);
            userOrderCreator.create(userOrderProperties, certificateIds, userIds);
        }
    }
}
