package com.petone.users.ms_users.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Desactivar CSRF para que Postman pueda hacer POST
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll() // Permitir todo sin token (SOLO PARA PRUEBAS)
            );
        return http.build();
    }
}
