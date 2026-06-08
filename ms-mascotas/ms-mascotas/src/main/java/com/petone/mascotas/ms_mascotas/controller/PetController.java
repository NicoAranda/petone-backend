package com.petone.mascotas.ms_mascotas.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.petone.mascotas.ms_mascotas.model.Pet;
import com.petone.mascotas.ms_mascotas.service.PetService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/mascotas")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PetController {
    
    private final PetService petService;

    @PostMapping
    public ResponseEntity<Pet> registrar(@RequestBody Pet dto){
        Pet pet = petService.addPet(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(pet);
    }

    @GetMapping
    public ResponseEntity<List<Pet>> listarMascotas(){
        List<Pet> pets = petService.viewAllPets();
        return ResponseEntity.ok(pets);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pet> mostrarMascota(@PathVariable Long id){
        Pet pet = petService.viewPetById(id);
        return ResponseEntity.ok(pet);
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<Pet>> listarPorUsuario(@PathVariable Long usuarioId){
        List<Pet> pets = petService.viewPetsByUsuarioId(usuarioId);
        return ResponseEntity.ok(pets);
    }

    @GetMapping("/mis-mascotas")
    public ResponseEntity<List<Pet>> listarMisMascotas(jakarta.servlet.http.HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        List<Pet> pets = petService.viewPetsByUsuarioId(userId);
        return ResponseEntity.ok(pets);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Pet> actualizarMascota(@PathVariable Long id, @RequestBody Pet dto){
        Pet pet = petService.updatePetById(id, dto);
        return ResponseEntity.ok(pet);
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<Pet> actualizarEstado(
        @PathVariable Long id, 
        @RequestParam String nuevoEstado
    ){
        Pet pet = petService.updateEstado(id, nuevoEstado);
        return ResponseEntity.ok(pet);
    }

    @DeleteMapping("/{id}") 
    public ResponseEntity<Void> eliminarMascota(@PathVariable Long id){
        petService.deletePet(id);
        return ResponseEntity.noContent().build();
    }
}