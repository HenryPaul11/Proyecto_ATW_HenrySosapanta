package com.powerfit.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.powerfit.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    @Bean
    public PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        http
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint((req, res, e) -> {
                    res.setStatus(401); res.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    res.getWriter().write(mapper.writeValueAsString(ApiResponse.error("Token inválido o no proporcionado", 401)));
                })
                .accessDeniedHandler((req, res, e) -> {
                    res.setStatus(403); res.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    res.getWriter().write(mapper.writeValueAsString(ApiResponse.error("Sin permisos", 403)));
                })
            )
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**","/swagger-ui.html","/swagger-ui/**","/api-docs/**","/v3/api-docs/**").permitAll()
                // Roles: ADMIN_MATRIZ, ADMIN_SUCURSAL, RECEPCIONISTA, ENTRENADOR, CLIENTE
                .requestMatchers("/api/sucursales/**").hasRole("ADMIN_MATRIZ")
                .requestMatchers("/api/admin/**").hasAnyRole("ADMIN_MATRIZ","ADMIN_SUCURSAL")
                .requestMatchers("/api/usuarios/**").hasAnyRole("ADMIN_MATRIZ","ADMIN_SUCURSAL")
                .requestMatchers("/api/auditorias/**").hasAnyRole("ADMIN_MATRIZ","ADMIN_SUCURSAL")
                .requestMatchers(HttpMethod.DELETE, "/api/**").hasAnyRole("ADMIN_MATRIZ","ADMIN_SUCURSAL")
                .requestMatchers("/api/sesiones/**").hasAnyRole("ADMIN_MATRIZ","ADMIN_SUCURSAL","RECEPCIONISTA","ENTRENADOR")
                .requestMatchers("/api/entrenadores/**").hasAnyRole("ADMIN_MATRIZ","ADMIN_SUCURSAL","RECEPCIONISTA","ENTRENADOR")
                .requestMatchers(HttpMethod.POST, "/api/clientes/**").hasAnyRole("ADMIN_MATRIZ","ADMIN_SUCURSAL","RECEPCIONISTA")
                .requestMatchers(HttpMethod.PUT,  "/api/clientes/**").hasAnyRole("ADMIN_MATRIZ","ADMIN_SUCURSAL","RECEPCIONISTA")
                .requestMatchers(HttpMethod.POST, "/api/membresias/**").hasAnyRole("ADMIN_MATRIZ","ADMIN_SUCURSAL","RECEPCIONISTA")
                .requestMatchers(HttpMethod.PUT,  "/api/membresias/**").hasAnyRole("ADMIN_MATRIZ","ADMIN_SUCURSAL","RECEPCIONISTA")
                .requestMatchers(HttpMethod.POST, "/api/pagos/**").hasAnyRole("ADMIN_MATRIZ","ADMIN_SUCURSAL","RECEPCIONISTA")
                .requestMatchers(HttpMethod.GET,  "/api/**").authenticated()
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
