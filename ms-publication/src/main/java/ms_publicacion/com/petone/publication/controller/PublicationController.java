package ms_publicacion.com.petone.publication.controller;

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

import lombok.RequiredArgsConstructor;

import ms_publicacion.com.petone.publication.model.Publication;
import ms_publicacion.com.petone.publication.service.PublicationService;

@RestController
@RequestMapping("/api/publicaciones")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PublicationController {

    private final PublicationService service;

    @PostMapping
    public ResponseEntity<Publication> crear(@RequestBody Publication dto) {
        Publication p = service.addPublication(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(p);
    }

    @GetMapping
    public ResponseEntity<List<Publication>> listar() {
        return ResponseEntity.ok(service.viewAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Publication> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(service.viewById(id));
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<Publication>> porUsuario(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(service.viewByUsuarioId(usuarioId));
    }

    @GetMapping("/mascota/{mascotaId}")
    public ResponseEntity<List<Publication>> porMascota(@PathVariable Long mascotaId) {
        return ResponseEntity.ok(service.viewByMascotaId(mascotaId));
    }

    @GetMapping("/ubicacion/{ubicacionId}")
    public ResponseEntity<List<Publication>> porUbicacion(@PathVariable Long ubicacionId) {
        return ResponseEntity.ok(service.viewByUbicacionId(ubicacionId));
    }

    @GetMapping("/estado")
    public ResponseEntity<List<Publication>> porEstado(@RequestParam String estado) {
        return ResponseEntity.ok(service.viewByEstado(estado));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Publication> actualizar(@PathVariable Long id, @RequestBody Publication dto) {
        return ResponseEntity.ok(service.updateById(id, dto));
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<Publication> actualizarEstado(@PathVariable Long id, @RequestParam String nuevoEstado) {
        return ResponseEntity.ok(service.updateEstado(id, nuevoEstado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

}
