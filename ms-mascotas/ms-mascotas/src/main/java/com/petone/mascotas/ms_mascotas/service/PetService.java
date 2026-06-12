package com.petone.mascotas.ms_mascotas.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.petone.mascotas.ms_mascotas.model.Pet;
import com.petone.mascotas.ms_mascotas.repository.PetRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PetService {

    private final PetRepository petRepository;

    public Pet addPet(Pet pet) {
        return petRepository.save(pet);
    }

    public List<Pet> viewAllPets() {
        return petRepository.findAll();
    }

    public Pet viewPetById(Long id) {
        return petRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mascota no encontrada con el id: " + id));
    }

    public List<Pet> viewPetsByUsuarioId(Long usuarioId) {
        return petRepository.findByUsuarioId(usuarioId);
    }

    public Pet updatePetById(Long id, Pet dto) {
        Pet existingPet = viewPetById(id);
        
        // Actualizamos los campos permitidos
        if (dto.getNombre() != null) existingPet.setNombre(dto.getNombre());
        if (dto.getRaza() != null) existingPet.setRaza(dto.getRaza());
        if (dto.getColor() != null) existingPet.setColor(dto.getColor());
        if (dto.getTamano() != null) existingPet.setTamano(dto.getTamano());
        if (dto.getEstado() != null) existingPet.setEstado(dto.getEstado());
        if (dto.getDescripcion() != null) existingPet.setDescripcion(dto.getDescripcion());
        if (dto.getFotoUrl() != null) existingPet.setFotoUrl(dto.getFotoUrl());
        
        return petRepository.save(existingPet);
    }

    public Pet updateEstado(Long id, String nuevoEstado) {
        Pet pet = viewPetById(id);
        pet.setEstado(nuevoEstado);
        return petRepository.save(pet);
    }

    public void deletePet(Long id) {
        petRepository.deleteById(id);
    }
}