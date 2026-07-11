package com.petone.users.ms_users.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.petone.users.ms_users.model.OrganizationRequest;
import com.petone.users.ms_users.service.OrganizationRequestService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/solicitudes-organizacion")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class OrganizationRequestController {

    private final OrganizationRequestService organizationRequestService;

    
    @PostMapping
    public ResponseEntity<OrganizationRequest> crearSolicitud(
            @RequestBody OrganizationRequest dto,
            @RequestParam Long usuarioId) {

        dto.setUsuarioId(usuarioId);

        OrganizationRequest solicitud =
                organizationRequestService.crearSolicitud(dto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(solicitud);
    }

    
    @GetMapping
    public ResponseEntity<List<OrganizationRequest>> listarSolicitudes() {

        return ResponseEntity.ok(
                organizationRequestService.listarSolicitudes()
        );
    }

  
    @GetMapping("/{id}")
    public ResponseEntity<OrganizationRequest> obtenerSolicitud(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                organizationRequestService.obtenerSolicitud(id)
        );
    }


    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<OrganizationRequest>> listarSolicitudesUsuario(
            @PathVariable Long usuarioId) {

        return ResponseEntity.ok(
                organizationRequestService.listarSolicitudesUsuario(usuarioId)
        );
    }


    @PutMapping("/{id}/aprobar")
    public ResponseEntity<OrganizationRequest> aprobarSolicitud(

            @PathVariable Long id,

            @RequestParam Long administradorId,

            @RequestParam(required = false) String respuesta) {

        OrganizationRequest solicitud =
                organizationRequestService.aprobarSolicitud(
                        id,
                        administradorId,
                        respuesta
                );

        return ResponseEntity.ok(solicitud);
    }


    @PutMapping("/{id}/rechazar")
    public ResponseEntity<OrganizationRequest> rechazarSolicitud(

            @PathVariable Long id,

            @RequestParam Long administradorId,

            @RequestParam String respuesta) {

        OrganizationRequest solicitud =
                organizationRequestService.rechazarSolicitud(
                        id,
                        administradorId,
                        respuesta
                );

        return ResponseEntity.ok(solicitud);
    }

}