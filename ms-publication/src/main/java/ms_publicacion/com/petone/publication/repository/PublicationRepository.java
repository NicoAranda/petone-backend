package ms_publicacion.com.petone.publication.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ms_publicacion.com.petone.publication.model.Publication;

@Repository
public interface PublicationRepository extends JpaRepository<Publication, Long> {

    @Query("select distinct p from Publication p left join fetch p.fotos")
    List<Publication> findAllWithFotos();

    @Query("select p from Publication p left join fetch p.fotos where p.id = :id")
    Optional<Publication> findByIdWithFotos(@Param("id") Long id);

}
