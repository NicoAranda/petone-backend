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
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import ms_publicacion.com.petone.publication.config.JwtService;
import ms_publicacion.com.petone.publication.model.Publication;
import ms_publicacion.com.petone.publication.service.PublicationService;

@RestController
@RequestMapping("/api/publicaciones")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PublicationController {

    private final PublicationService service;
    private final JwtService jwtService;

    @PostMapping
    public ResponseEntity<Publication> crear(@RequestBody Publication dto) {
        Publication p = service.addPublication(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(p);
    }

    @PostMapping(value = "/con-imagenes", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Publication> crearConImagenes(
            HttpServletRequest request,
            @RequestParam String nombre,
            @RequestParam String ubicacion,
            @RequestParam String especie,
            @RequestParam String sexo,
            @RequestParam String estado,
            @RequestParam String descripcion,
            @RequestParam List<MultipartFile> fotos
    ) {

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String token = authHeader.substring(7);
        Long userId = jwtService.extractUserId(token);

        Publication p = new Publication();
        p.setNombre(nombre);
        p.setUserId(userId);
        p.setUbicacion(ubicacion);
        p.setEspecie(especie);
        p.setSexo(sexo);
        p.setEstado(estado);
        p.setDescripcion(descripcion);
        Publication saved = service.crearConImagenes(p, fotos);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(saved);
    }

    @GetMapping
    public ResponseEntity<List<Publication>> listar() {
        return ResponseEntity.ok(service.viewAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Publication> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(service.viewById(id));
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
