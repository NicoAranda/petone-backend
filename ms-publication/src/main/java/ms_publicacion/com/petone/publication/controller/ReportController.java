package ms_publicacion.com.petone.publication.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import ms_publicacion.com.petone.publication.model.Report;
import ms_publicacion.com.petone.publication.model.Report.ReportStatus;
import ms_publicacion.com.petone.publication.service.ReportService;

@RestController
@RequestMapping("/api/reportes")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ReportController {

    private final ReportService reportService;

    @PostMapping
    public ResponseEntity<Report> crearReporte(@RequestBody Report reporte) {
        Report nuevoReporte = reportService.crearReporte(reporte);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoReporte);
    }

    @GetMapping
    public ResponseEntity<List<Report>> obtenerTodos() {
        List<Report> reportes = reportService.obtenerTodos();
        return ResponseEntity.ok(reportes);
    }

    @GetMapping("/pendientes")
    public ResponseEntity<List<Report>> obtenerPendientes() {
        List<Report> reportes = reportService.obtenerPendientes();
        return ResponseEntity.ok(reportes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Report> obtenerPorId(@PathVariable Long id) {
        Report reporte = reportService.obtenerPorId(id);
        return ResponseEntity.ok(reporte);
    }

    @GetMapping("/publicacion/{publicacionId}")
    public ResponseEntity<List<Report>> obtenerPorPublicacion(@PathVariable Long publicacionId) {
        List<Report> reportes = reportService.obtenerPorPublicacion(publicacionId);
        return ResponseEntity.ok(reportes);
    }

    @PutMapping("/{id}/estado")
    public ResponseEntity<Report> actualizarEstado(
            @PathVariable Long id,
            @RequestParam ReportStatus nuevoEstado) {
        Report reporte = reportService.actualizarEstado(id, nuevoEstado);
        return ResponseEntity.ok(reporte);
    }

    @PutMapping("/{id}/responder")
    public ResponseEntity<Report> responderReporte(
            @PathVariable Long id,
            @RequestBody ReporteRespuestaDTO dto) {
        Report reporte = reportService.responderReporte(id, dto.getRespuesta(), dto.getEstado());
        return ResponseEntity.ok(reporte);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarReporte(@PathVariable Long id) {
        reportService.eliminarReporte(id);
        return ResponseEntity.noContent().build();
    }

    public static class ReporteRespuestaDTO {
        private String respuesta;
        private ReportStatus estado;

        public ReporteRespuestaDTO() {}

        public ReporteRespuestaDTO(String respuesta, ReportStatus estado) {
            this.respuesta = respuesta;
            this.estado = estado;
        }

        public String getRespuesta() {
            return respuesta;
        }

        public void setRespuesta(String respuesta) {
            this.respuesta = respuesta;
        }

        public ReportStatus getEstado() {
            return estado;
        }

        public void setEstado(ReportStatus estado) {
            this.estado = estado;
        }
    }
}
