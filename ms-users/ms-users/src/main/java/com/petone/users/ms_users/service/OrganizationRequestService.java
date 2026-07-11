package com.petone.users.ms_users.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.petone.users.ms_users.model.OrganizationRequest;
import com.petone.users.ms_users.model.User;
import com.petone.users.ms_users.repository.OrganizationRequestRepository;
import com.petone.users.ms_users.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrganizationRequestService {

    private final OrganizationRequestRepository orRequestRepository;
    private final UserRepository userRepository;
    private final UserService userService;

    public OrganizationRequest crearSolicitud(OrganizationRequest dto) {

        User usuario = userRepository.findById(dto.getUsuarioId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Usuario no encontrado"));

        if (orRequestRepository.findByUsuarioIdAndEstado(
                usuario.getId(),
                "PENDIENTE").isPresent()) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Ya existe una solicitud pendiente.");
        }

        dto.setEstado("PENDIENTE");
        dto.setFechaSolicitud(LocalDateTime.now());

        return orRequestRepository.save(dto);
    }

    public List<OrganizationRequest> listarSolicitudes() {
        return orRequestRepository.findAll();
    }

    public OrganizationRequest obtenerSolicitud(Long id) {

        return orRequestRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Solicitud no encontrada"));
    }

    public List<OrganizationRequest> listarSolicitudesUsuario(Long usuarioId) {

        return orRequestRepository.findByUsuarioId(usuarioId);

    }

    public OrganizationRequest aprobarSolicitud(
            Long solicitudId,
            Long administradorId,
            String respuesta) {

        OrganizationRequest solicitud = obtenerSolicitud(solicitudId);

        if (!"PENDIENTE".equals(solicitud.getEstado())) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La solicitud ya fue procesada.");

        }

        User usuario = userRepository.findById(solicitud.getUsuarioId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Usuario no encontrado"));

        userService.actualizarRol(
                solicitud.getUsuarioId(),
                "ORGANIZACION");

        solicitud.setEstado("APROBADA");
        solicitud.setRespuestaAdministrador(respuesta);
        solicitud.setAdministradorId(administradorId);
        solicitud.setFechaRevision(LocalDateTime.now());

        return orRequestRepository.save(solicitud);
    }

    public OrganizationRequest rechazarSolicitud(
            Long solicitudId,
            Long administradorId,
            String respuesta) {

        OrganizationRequest solicitud = obtenerSolicitud(solicitudId);

        if (!"PENDIENTE".equals(solicitud.getEstado())) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La solicitud ya fue procesada.");

        }

        solicitud.setEstado("RECHAZADA");
        solicitud.setRespuestaAdministrador(respuesta);
        solicitud.setAdministradorId(administradorId);
        solicitud.setFechaRevision(LocalDateTime.now());

        return orRequestRepository.save(solicitud);
    }

}
