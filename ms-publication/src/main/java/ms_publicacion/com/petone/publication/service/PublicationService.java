package ms_publicacion.com.petone.publication.service;

import java.util.List;

import org.springframework.stereotype.Service;

import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;

import ms_publicacion.com.petone.publication.model.Publication;
import ms_publicacion.com.petone.publication.repository.PublicationRepository;

@Service
@RequiredArgsConstructor
public class PublicationService {

    private final PublicationRepository repo;
    private final SupabaseStorageService storageService;

    public Publication addPublication(Publication p) {
        if (p == null) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.BAD_REQUEST, "Publicación inválida");
        }


        if (p.getDescripcion() == null || p.getDescripcion().isBlank()) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.BAD_REQUEST, "La descripción es obligatoria");
        }


        // Default fechaPublicacion a ahora si no está seteada
        if (p.getFechaPublicacion() == null) {
            p.setFechaPublicacion(new java.util.Date());
        }

        // Estado por defecto
        if (p.getEstado() == null || p.getEstado().isBlank()) {
            p.setEstado("ACTIVA");
        }

        return repo.save(p);
    }

    public Publication crearConImagenes(
            Publication dto,
            List<MultipartFile> imagenes) {

        try {

            List<String> urls = imagenes.stream()
                    .map(img -> {
                        try {
                            return storageService.uploadFile(img);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .toList();

            dto.setFotos(urls);

            // Default fechaPublicacion a ahora si no está seteada
            if (dto.getFechaPublicacion() == null) {
                dto.setFechaPublicacion(new java.util.Date());
            }

            // Estado por defecto
            if (dto.getEstado() == null || dto.getEstado().isBlank()) {
                dto.setEstado("ACTIVA");
            }

            return repo.save(dto);

        } catch (Exception e) {
            throw new RuntimeException("Error creando publicación");
        }
    }

    public List<Publication> viewAll() {
        return repo.findAllWithFotos();
    }

    public Publication viewById(Long id) {
        return repo.findByIdWithFotos(id)
                .orElseThrow(() -> new RuntimeException("Publicación no encontrada: " + id));
    }

    public Publication updateById(Long id, Publication dto) {
        Publication existing = viewById(id);
        existing.setNombre(dto.getNombre());
        existing.setUbicacion(dto.getUbicacion());
        existing.setEspecie(dto.getEspecie());
        existing.setSexo(dto.getSexo());
        existing.setEstado(dto.getEstado());
        existing.setDescripcion(dto.getDescripcion());
        return repo.save(existing);
    }

    public Publication updateEstado(Long id, String nuevoEstado) {
        Publication p = viewById(id);
        p.setEstado(nuevoEstado);
        return repo.save(p);
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }
}
