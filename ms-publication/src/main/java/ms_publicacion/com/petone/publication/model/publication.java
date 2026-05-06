package ms_publicacion.com.petone.publication.model;

import java.util.Date;
import java.util.List;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "publicaciones")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Publication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String tipo;
    private String titulo;
    private String descripcion;

    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaPublicacion;

    private String estado;

    // Información relacionada con la mascota y el usuario
    private Long mascotaId;
    private Long usuarioId;

    // Ubicación explícita en la publicación
    private Double latitud;
    private Double longitud;
    private String direccion;

    // Lista de URLs de fotos asociadas a la publicación
    @ElementCollection
    @CollectionTable(name = "publicacion_fotos", joinColumns = @JoinColumn(name = "publication_id"))
    @Column(name = "foto_url")
    private List<String> fotos;

    // Opcional: referencia a una entidad de ubicaciones por id
    private Long ubicacionId;
}
