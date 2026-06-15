package com.petone.mascotas.ms_mascotas.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.petone.mascotas.ms_mascotas.model.Pet;
import com.petone.mascotas.ms_mascotas.service.PetService;

import jakarta.servlet.http.HttpServletRequest;

public class PetControllerTest {

    private PetService petService;
    private PetController controller;

    @BeforeEach
    void setup() {
        petService = Mockito.mock(PetService.class);
        controller = new PetController(petService);
    }

    @Test
    void registrar_mascota_devuelve_created() {
        HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
        when(req.getAttribute("userId")).thenReturn(101L);

        Pet saved = new Pet(1L, "Firulais", "Labrador", "Dorado", "Grande", "DISPONIBLE", "Buen perro", 101L, null);

        when(petService.addPet(any(Pet.class))).thenReturn(saved);

        ResponseEntity<Pet> resp = controller.registrar("Firulais", "Labrador", "Dorado", "Grande", "DISPONIBLE", "Buen perro", null, null, req);

        assertEquals(HttpStatus.CREATED, resp.getStatusCode());
        assertEquals("Firulais", resp.getBody().getNombre());
    }

    @Test
    void listarMisMascotas_sin_userid_devuelve_unauthorized() {
        HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
        when(req.getAttribute("userId")).thenReturn(null);

        ResponseEntity<List<Pet>> resp = controller.listarMisMascotas(req);
        assertEquals(HttpStatus.UNAUTHORIZED, resp.getStatusCode());
    }
}
