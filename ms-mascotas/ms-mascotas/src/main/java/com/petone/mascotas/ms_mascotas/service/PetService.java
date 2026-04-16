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
        existingPet.setNombre(dto.getNombre());
        existingPet.setRaza(dto.getRaza());
        existingPet.setColor(dto.getColor());
        existingPet.setTamano(dto.getTamano());
        existingPet.setEstado(dto.getEstado());
        existingPet.setDescripcion(dto.getDescripcion());
        
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