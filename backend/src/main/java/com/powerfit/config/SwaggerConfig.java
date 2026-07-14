package com.powerfit.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI powerfitOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("PowerFit API")
                        .description("Sistema de Gestión para Gimnasio PowerFit – API REST")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("PowerFit Dev Team")
                                .email("dev@powerfit.com")));
    }
}
