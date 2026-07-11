package com.petone.users.ms_users.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "solicitudes_organizacion")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrganizationRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long usuarioId;
    private String nombreOrganizacion;
    private String tipoOrganizacion;
    private String correoInstitucional;
    private String telefono;
    private String direccion;
    private String sitioWeb;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(columnDefinition = "TEXT")
    private String motivoSolicitud;

    private String estado;

    @Column(columnDefinition = "TEXT")
    private String respuestaAdministrador;

    private LocalDateTime fechaSolicitud;
    private LocalDateTime fechaRevision;
    private Long administradorId;
}
