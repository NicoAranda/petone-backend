package com.petone.mascotas.ms_mascotas.controller;

import java.util.List;
import java.util.stream.Collectors;

import java.io.IOException;
import java.util.Base64;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;

import com.petone.mascotas.ms_mascotas.model.Pet;
import com.petone.mascotas.ms_mascotas.service.PetService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/mascotas")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PetController {
    
    private final PetService petService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Pet> registrar(
            @RequestParam String nombre,
            @RequestParam(required = false) String raza,
            @RequestParam(required = false) String color,
            @RequestParam(required = false) String tamano,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) String descripcion,
            @RequestParam(required = false) Long usuarioId,
            @RequestParam(required = false) MultipartFile[] fotos,
            jakarta.servlet.http.HttpServletRequest request) {
        
        if (usuarioId == null) {
            usuarioId = (Long) request.getAttribute("userId");
        }
        
        System.out.println("Creating pet: " + nombre + " for usuario: " + usuarioId);
        
        Pet pet = new Pet();
        pet.setNombre(nombre);
        pet.setRaza(raza != null ? raza : "No especificada");
        pet.setColor(color != null ? color : "No especificado");
        pet.setTamano(tamano != null ? tamano : "Medio");
        pet.setEstado(estado != null ? estado : "Activo");
        pet.setDescripcion(descripcion);
        pet.setUsuarioId(usuarioId);

        if (fotos != null && fotos.length > 0) {
            try {
                pet.setFotoUrl(convertToDataUrl(fotos[0]));
            } catch (IOException e) {
                System.err.println("Error processing pet photo: " + e.getMessage());
            }
        }
        
        Pet createdPet = petService.addPet(pet);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPet);
    }

    private String convertToDataUrl(MultipartFile file) throws IOException {
        String contentType = file.getContentType() != null ? file.getContentType() : "image/jpeg";
        String base64 = Base64.getEncoder().encodeToString(file.getBytes());
        return "data:" + contentType + ";base64," + base64;
    }

    private boolean esMascotaVisibleEnPerfil(Pet pet) {
        String estado = pet.getEstado();
        return estado == null || (!"Perdido".equalsIgnoreCase(estado) && !"Reportado".equalsIgnoreCase(estado));
    }

    @GetMapping
    public ResponseEntity<List<Pet>> listarMascotas(){
        List<Pet> pets = petService.viewAllPets().stream()
                .filter(this::esMascotaVisibleEnPerfil)
                .collect(Collectors.toList());
        return ResponseEntity.ok(pets);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pet> mostrarMascota(@PathVariable Long id){
        Pet pet = petService.viewPetById(id);
        return ResponseEntity.ok(pet);
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<Pet>> listarPorUsuario(@PathVariable Long usuarioId){
        System.out.println("Fetching pets for usuario: " + usuarioId);
        List<Pet> pets = petService.viewPetsByUsuarioId(usuarioId).stream()
                .filter(this::esMascotaVisibleEnPerfil)
                .collect(Collectors.toList());
        System.out.println("Found " + pets.size() + " pets");
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

    @PostMapping("/{id}/like")
    public ResponseEntity<Pet> darLike(@PathVariable Long id) {
        Pet pet = petService.incrementLikes(id);
        return ResponseEntity.ok(pet);
    }

    @DeleteMapping("/{id}/like")
    public ResponseEntity<Pet> quitarLike(@PathVariable Long id) {
        Pet pet = petService.decrementLikes(id);
        return ResponseEntity.ok(pet);
    }

    @DeleteMapping("/{id}") 
    public ResponseEntity<Void> eliminarMascota(@PathVariable Long id){
        petService.deletePet(id);
        return ResponseEntity.noContent().build();
    }
}