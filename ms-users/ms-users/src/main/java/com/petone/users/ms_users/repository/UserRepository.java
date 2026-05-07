package com.petone.users.ms_users.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.petone.users.ms_users.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long>{
    boolean existsByEmail(String email);
    Optional<User> findByEmail(String email);
    boolean existsByRut(String rut);
    Optional<User> findByRut(String rut);
}
