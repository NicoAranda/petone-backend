package ms_publicacion.com.petone.publication.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ms_publicacion.com.petone.publication.model.Report;
import ms_publicacion.com.petone.publication.model.Report.ReportStatus;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {
    List<Report> findByPublicacionId(Long publicacionId);
    List<Report> findByEstado(ReportStatus estado);
    List<Report> findByPublicacionIdAndEstado(Long publicacionId, ReportStatus estado);
}
