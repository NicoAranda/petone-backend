package ms_publicacion.com.petone.publication.model;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "reportes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long publicacionId;
    private Long usuarioId;

    @Enumerated(EnumType.STRING)
    private ReportType tipo;

    @Column(length = 500)
    private String razon;

    @Column(length = 2000)
    private String descripcion;

    @Enumerated(EnumType.STRING)
    private ReportStatus estado;

    @Column(length = 1000)
    private String respuestaAdmin;

    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaReporte;

    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaRespuesta;

    public enum ReportType {
        ABUSO("Abuso o acoso"),
        IMAGENES_SENSIBLES("Imágenes sensibles"),
        CONTENIDO_INAPROPIADO("Contenido inapropiado"),
        INFORMACION_FALSA("Información falsa"),
        OTRO("Otro");

        private final String label;

        ReportType(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }
    }

    public enum ReportStatus {
        PENDIENTE("Pendiente"),
        EN_REVISION("En revisión"),
        RESUELTO("Resuelto"),
        RECHAZADO("Rechazado");

        private final String label;

        ReportStatus(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }
    }
}
