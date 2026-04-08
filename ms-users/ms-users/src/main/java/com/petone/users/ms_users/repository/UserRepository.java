package com.petone.users.ms_users.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.petone.users.ms_users.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long>{
    
}
