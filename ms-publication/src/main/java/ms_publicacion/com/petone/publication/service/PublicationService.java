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
        return repo.save(p);
    }

    public List<Publication> viewAll() {
        return repo.findAll();
    }

    public Publication viewById(Long id) {
        return repo.findById(id)
            .orElseThrow(() -> new RuntimeException("Publicación no encontrada: " + id));
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
