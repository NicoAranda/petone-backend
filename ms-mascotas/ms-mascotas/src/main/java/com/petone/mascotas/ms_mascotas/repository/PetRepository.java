package com.petone.mascotas.ms_mascotas.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.petone.mascotas.ms_mascotas.model.Pet;

@Repository
public interface PetRepository extends JpaRepository<Pet, Long> {
    
    // Spring Data JPA crea la consulta automáticamente con solo nombrar bien el método
    List<Pet> findByUsuarioId(Long usuarioId);
    
}