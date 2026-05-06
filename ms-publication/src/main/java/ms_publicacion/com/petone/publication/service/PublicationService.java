package ms_publicacion.com.petone.publication.service;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import ms_publicacion.com.petone.publication.model.Publication;
import ms_publicacion.com.petone.publication.repository.PublicationRepository;

@Service
@RequiredArgsConstructor
public class PublicationService {

    private final PublicationRepository repo;

    public Publication addPublication(Publication p) {
        if (p == null) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.BAD_REQUEST, "Publicación inválida");
        }

        if (p.getTipo() == null || p.getTipo().isBlank()) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.BAD_REQUEST, "El tipo de publicación es obligatorio");
        }

        if (p.getTitulo() == null || p.getTitulo().isBlank()) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.BAD_REQUEST, "El título es obligatorio");
        }

        if (p.getDescripcion() == null || p.getDescripcion().isBlank()) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.BAD_REQUEST, "La descripción es obligatoria");
        }

        if (p.getUsuarioId() == null) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.BAD_REQUEST, "El id del usuario es obligatorio");
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

    public List<Publication> viewAll() {
        return repo.findAll();
    }

    public Publication viewById(Long id) {
        return repo.findById(id)
            .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(
                org.springframework.http.HttpStatus.NOT_FOUND, "Publicación no encontrada: " + id));
    }

    public List<Publication> viewByUsuarioId(Long usuarioId) {
        return repo.findByUsuarioId(usuarioId);
    }

    public List<Publication> viewByMascotaId(Long mascotaId) {
        return repo.findByMascotaId(mascotaId);
    }

    public List<Publication> viewByUbicacionId(Long ubicacionId) {
        return repo.findByUbicacionId(ubicacionId);
    }

    public List<Publication> viewByEstado(String estado) {
        return repo.findByEstado(estado);
    }

    public Publication updateById(Long id, Publication dto) {
        Publication existing = viewById(id);
        existing.setTipo(dto.getTipo());
        existing.setTitulo(dto.getTitulo());
        existing.setDescripcion(dto.getDescripcion());
        existing.setFechaPublicacion(dto.getFechaPublicacion());
        existing.setEstado(dto.getEstado());
        existing.setMascotaId(dto.getMascotaId());
        existing.setUsuarioId(dto.getUsuarioId());
        existing.setUbicacionId(dto.getUbicacionId());
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
