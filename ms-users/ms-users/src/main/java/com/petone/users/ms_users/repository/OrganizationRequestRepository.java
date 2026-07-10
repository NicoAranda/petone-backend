package com.petone.users.ms_users.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.petone.users.ms_users.model.OrganizationRequest;

public interface OrganizationRequestRepository extends JpaRepository<OrganizationRequest, Long>{

    List<OrganizationRequest> findByEstado(String estado);
    Optional<OrganizationRequest> findByUsuarioIdAndEstado(Long usuarioId, String estado);
    List<OrganizationRequest> findByUsuarioId(Long usuarioId);
    
}
