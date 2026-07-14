package com.powerfit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * Clase principal de PowerFit Backend.
 * Extiende SpringBootServletInitializer para soportar empaquetado WAR
 * y despliegue en servidores externos (Tomcat, WildFly, etc.).
 */
@SpringBootApplication
public class PowerFitApplication extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(PowerFitApplication.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(PowerFitApplication.class, args);
    }
}
