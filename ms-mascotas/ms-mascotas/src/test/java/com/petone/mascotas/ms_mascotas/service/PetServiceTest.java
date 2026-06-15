package com.petone.mascotas.ms_mascotas.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.petone.mascotas.ms_mascotas.model.Pet;
import com.petone.mascotas.ms_mascotas.repository.PetRepository;

@ExtendWith(MockitoExtension.class)
class PetServiceTest {

    @Mock
    private PetRepository petRepository;

    @InjectMocks
    private PetService petService;

    private Pet samplePet;

    @BeforeEach
    void setUp() {
        samplePet = new Pet(1L, "Firulais", "Labrador", "Dorado", "Grande", "DISPONIBLE", "Perro amigable", 101L, null);
    }

    @Test
    void addPet_ShouldReturnSavedPet() {
        when(petRepository.save(any(Pet.class))).thenReturn(samplePet);

        Pet savedPet = petService.addPet(samplePet);

        assertNotNull(savedPet);
        assertEquals("Firulais", savedPet.getNombre());
        verify(petRepository, times(1)).save(samplePet);
    }

    @Test
    void viewAllPets_ShouldReturnList() {
        List<Pet> pets = Arrays.asList(samplePet);
        when(petRepository.findAll()).thenReturn(pets);

        List<Pet> result = petService.viewAllPets();

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        verify(petRepository, times(1)).findAll();
    }

    @Test
    void viewPetById_WhenExists_ShouldReturnPet() {
        when(petRepository.findById(1L)).thenReturn(Optional.of(samplePet));

        Pet foundPet = petService.viewPetById(1L);

        assertNotNull(foundPet);
        assertEquals(1L, foundPet.getId());
    }

    @Test
    void viewPetById_WhenNotExists_ShouldThrowException() {
        when(petRepository.findById(2L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            petService.viewPetById(2L);
        });

        assertTrue(exception.getMessage().contains("Mascota no encontrada"));
    }

    @Test
    void updatePetById_ShouldReturnUpdatedPet() {
        Pet updateDto = new Pet();
        updateDto.setNombre("Rex");
        updateDto.setRaza("Pastor Aleman");

        when(petRepository.findById(1L)).thenReturn(Optional.of(samplePet));
        when(petRepository.save(any(Pet.class))).thenReturn(samplePet);

        Pet updatedPet = petService.updatePetById(1L, updateDto);

        assertNotNull(updatedPet);
        assertEquals("Rex", updatedPet.getNombre());
        assertEquals("Pastor Aleman", updatedPet.getRaza());
        verify(petRepository, times(1)).save(any(Pet.class));
    }

    @Test
    void updateEstado_ShouldChangeStatus() {
        when(petRepository.findById(1L)).thenReturn(Optional.of(samplePet));
        when(petRepository.save(any(Pet.class))).thenReturn(samplePet);

        Pet result = petService.updateEstado(1L, "ADOPTADO");

        assertEquals("ADOPTADO", result.getEstado());
        verify(petRepository, times(1)).save(any(Pet.class));
    }

    @Test
    void deletePet_ShouldCallRepository() {
        doNothing().when(petRepository).deleteById(1L);

        petService.deletePet(1L);

        verify(petRepository, times(1)).deleteById(1L);
    }
}
