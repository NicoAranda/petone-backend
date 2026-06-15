package com.petone.users.ms_users.config;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.petone.users.ms_users.model.User;

public class JwtServiceTest {

    @Test
    void generate_and_extract_claims() {
        JwtService jwtService = new JwtService();

        User user = User.builder()
                .id(123L)
                .email("test@example.com")
                .nombre("Nombre")
                .apellido("Apellido")
                .rol("CLIENTE")
                .build();

        String token = jwtService.generateToken(user);

        assertEquals("test@example.com", jwtService.extractUsername(token));
        assertEquals(123L, jwtService.extractUserId(token).longValue());
        assertEquals("CLIENTE", jwtService.extractRole(token));
    }
}
