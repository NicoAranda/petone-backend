package com.petone.users.ms_users.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.petone.users.ms_users.model.User;
import com.petone.users.ms_users.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService{

    private final UserRepository userRepository;

    public User addUser(User dto){

        if (dto.getNombre() == null || dto.getNombre().isBlank()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El nombre del usuario es obligatorio");
        }

        if (dto.getEmail() == null || dto.getEmail().isBlank()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El email es obligatorio");
        }

        if (!dto.getEmail().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El correo no es válido");
        }

        if (dto.getPassword() == null || dto.getPassword().isBlank()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La contraseña es obligatoria");
        }

        if (dto.getPassword().length() < 6){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La contraseña debe tener mas de 6 caracteres");
        }

        if (dto.getRol() == null || dto.getRol().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El rol es obligatorio");
        }
        if (!dto.getRol().equalsIgnoreCase("ADMIN") && !dto.getRol().equalsIgnoreCase("CLIENTE")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El rol debe ser ADMIN o CLIENTE");
        }

        User user = User.builder()
                .nombre(dto.getNombre())
                .email(dto.getEmail())
                .password(dto.getPassword())
                .rol(dto.getRol().toUpperCase())
                .saldoMonedas(dto.getSaldoMonedas())
                .build();

        return userRepository.save(user);
    }



}
