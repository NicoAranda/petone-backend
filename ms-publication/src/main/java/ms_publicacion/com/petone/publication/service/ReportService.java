package ms_publicacion.com.petone.publication.service;

import java.util.Date;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import lombok.RequiredArgsConstructor;
import ms_publicacion.com.petone.publication.model.Report;
import ms_publicacion.com.petone.publication.model.Report.ReportStatus;
import ms_publicacion.com.petone.publication.repository.ReportRepository;
import ms_publicacion.com.petone.publication.repository.PublicationRepository;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;
    private final PublicationRepository publicationRepository;

    public Report crearReporte(Report reporte) {
        if (reporte.getPublicacionId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ID de publicación es obligatorio");
        }

        if (!publicationRepository.existsById(reporte.getPublicacionId())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Publicación no encontrada");
        }

        if (reporte.getTipo() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tipo de reporte es obligatorio");
        }

        if (reporte.getDescripcion() == null || reporte.getDescripcion().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Descripción es obligatoria");
        }

        reporte.setEstado(ReportStatus.PENDIENTE);
        reporte.setFechaReporte(new Date());

        return reportRepository.save(reporte);
    }

    public List<Report> obtenerTodos() {
        return reportRepository.findAll();
    }

    public List<Report> obtenerPendientes() {
        return reportRepository.findByEstado(ReportStatus.PENDIENTE);
    }

    public Report obtenerPorId(Long id) {
        return reportRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reporte no encontrado"));
    }

    public Report actualizarEstado(Long id, ReportStatus nuevoEstado) {
        Report reporte = obtenerPorId(id);
        reporte.setEstado(nuevoEstado);
        return reportRepository.save(reporte);
    }

    public Report responderReporte(Long id, String respuesta, ReportStatus estado) {
        Report reporte = obtenerPorId(id);
        reporte.setRespuestaAdmin(respuesta);
        reporte.setEstado(estado);
        reporte.setFechaRespuesta(new Date());
        return reportRepository.save(reporte);
    }

    public void eliminarReporte(Long id) {
        Report reporte = obtenerPorId(id);
        reportRepository.delete(reporte);
    }

    public List<Report> obtenerPorPublicacion(Long publicacionId) {
        return reportRepository.findByPublicacionId(publicacionId);
    }
}
