package com.petone.users.ms_users.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.petone.users.ms_users.config.JwtService;
import com.petone.users.ms_users.model.User;
import com.petone.users.ms_users.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public User addUser(User dto) {

        if (dto.getNombre() == null || dto.getNombre().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El nombre del usuario es obligatorio");
        }

        if (dto.getEmail() == null || dto.getEmail().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El email es obligatorio");
        }

        if (!dto.getEmail().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El correo no es válido");
        }

        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El email ya está registrado");
        }

        if (dto.getPassword() == null || dto.getPassword().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La contraseña es obligatoria");
        }

        if (dto.getPassword().length() < 6) {
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
                .password(passwordEncoder.encode(dto.getPassword()))
                .rol(dto.getRol().toUpperCase())
                .saldoMonedas(dto.getSaldoMonedas() != null ? dto.getSaldoMonedas() : 0)
                .build();

        return userRepository.save(user);
    }

    public List<User> viewAllUsers() {
        return userRepository.findAll();
    }

    public User viewUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        return user;
    }

    public User updateUserById(Long id, User dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        if (dto.getNombre() != null)
            user.setNombre(dto.getNombre());
        if (dto.getEmail() != null) {
            if (!dto.getEmail().equals(user.getEmail()) && userRepository.existsByEmail(dto.getEmail())) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Email ya en uso");
            }
            user.setEmail(dto.getEmail());
        }

        return userRepository.save(user);
    }

    public User updateSaldo(Long id, Integer nuevaCantidad) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        if (nuevaCantidad == null || nuevaCantidad < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Saldo inválido");
        }

        user.setSaldoMonedas(nuevaCantidad);
        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        userRepository.delete(user);
    }

    public String login(User dto) {
        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales inválidas"));

        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales inválidas");
        }

        return jwtService.generateToken(user);
    }

}
