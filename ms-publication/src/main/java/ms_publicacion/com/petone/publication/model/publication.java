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

    private String nombre;
    private String ubicacion;
    private String especie;
    private String sexo;
    private String estado;
    @Column(length = 2000)
    private String descripcion;

    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaPublicacion;

    // Lista de URLs de fotos asociadas a la publicación
    @ElementCollection
    @CollectionTable(name = "publicacion_fotos", joinColumns = @JoinColumn(name = "publication_id"))
    @Column(name = "foto_url", length = 1000)
    private List<String> fotos;
}
