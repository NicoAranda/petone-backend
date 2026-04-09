package com.petone.users.ms_users.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
            // Públicos
            .requestMatchers("/api/usuarios/login").permitAll()
            .requestMatchers(HttpMethod.POST, "/api/usuarios").permitAll()
            
            // Solo Administradores
            .requestMatchers(HttpMethod.GET, "/api/usuarios").hasRole("ADMIN")
            .requestMatchers(HttpMethod.DELETE, "/api/usuarios/**").hasRole("ADMIN")
            .requestMatchers("/api/usuarios/*/saldo").hasRole("ADMIN")
            
            .requestMatchers(HttpMethod.GET, "/api/usuarios/{id}").authenticated()
            .requestMatchers(HttpMethod.PATCH, "/api/usuarios/{id}").authenticated()
            
            .anyRequest().authenticated()
        )
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
