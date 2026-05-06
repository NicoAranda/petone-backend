package ms_publicacion.com.petone.publication.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ms_publicacion.com.petone.publication.model.Publication;

@Repository
public interface PublicationRepository extends JpaRepository<Publication, Long> {

    List<Publication> findByUsuarioId(Long usuarioId);

    List<Publication> findByMascotaId(Long mascotaId);

    List<Publication> findByUbicacionId(Long ubicacionId);

    List<Publication> findByEstado(String estado);

}
