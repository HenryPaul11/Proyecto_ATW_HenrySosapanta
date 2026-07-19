package com.powerfit.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.hibernate5.jakarta.Hibernate5JakartaModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class JacksonConfig {

    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();

        // Módulo para manejar proxies LAZY de Hibernate
        Hibernate5JakartaModule hibernateModule = new Hibernate5JakartaModule();
        // No forzar inicialización de proxies — serializa como null si no está cargado
        hibernateModule.disable(Hibernate5JakartaModule.Feature.FORCE_LAZY_LOADING);
        // Permite serializar proxies sin forzar carga
        hibernateModule.enable(Hibernate5JakartaModule.Feature.REPLACE_PERSISTENT_COLLECTIONS);

        // Módulo para fechas Java 8 (LocalDate, LocalDateTime)
        JavaTimeModule javaTimeModule = new JavaTimeModule();

        mapper.registerModule(hibernateModule);
        mapper.registerModule(javaTimeModule);
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);

        return mapper;
    }
}
