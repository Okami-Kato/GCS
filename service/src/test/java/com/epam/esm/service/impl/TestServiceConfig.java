package com.epam.esm.service.impl;

import com.epam.esm.entity.Certificate;
import com.epam.esm.entity.User;
import com.epam.esm.entity.UserOrder;
import com.epam.esm.service.dto.response.CertificateResponse;
import com.epam.esm.service.dto.response.UserOrderResponse;
import org.modelmapper.Condition;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@SpringBootConfiguration
public class TestServiceConfig {
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setSkipNullEnabled(true);

        Condition<?, ?> notNull = ctx -> ctx.getSource() != null;
        Converter<Instant, LocalDateTime> toLocalDate = ctx -> LocalDateTime.ofInstant(ctx.getSource(), ZoneId.systemDefault());

        mapper.typeMap(Certificate.class, CertificateResponse.class)
                .addMappings(m -> m.when(notNull).using(toLocalDate).map(Certificate::getCreateDate, CertificateResponse::setCreateDate))
                .addMappings(m -> m.when(notNull).using(toLocalDate).map(Certificate::getLastUpdateDate, CertificateResponse::setLastUpdateDate));
        mapper.typeMap(UserOrder.class, UserOrderResponse.class)
                .addMappings(m -> m.when(notNull).using(toLocalDate).map(UserOrder::getTimestamp, UserOrderResponse::setTimestamp));
        return mapper;
    }
}
