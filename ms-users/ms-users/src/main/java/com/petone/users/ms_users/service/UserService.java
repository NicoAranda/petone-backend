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
        if (dto.getApellido() == null || dto.getApellido().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El apellido del usuario es obligatorio");
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
        if (!dto.getRol().equalsIgnoreCase("ADMIN")
                && !dto.getRol().equalsIgnoreCase("CLIENTE")
                && !dto.getRol().equalsIgnoreCase("ORGANIZACION")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El rol debe ser ADMIN,  CLIENTE u ORGANIZACION");
        }

        if (dto.getRut() == null || dto.getRut().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El rut es obligatorio");
        }
        if (userRepository.existsByRut(dto.getRut())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El rut ya está registrado");
        }

        if (dto.getTelefono() == null || dto.getTelefono().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El número de teléfono es obligatorio");
        }

        User user = User.builder()
                .nombre(dto.getNombre())
                .apellido(dto.getApellido())
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .rol(dto.getRol().toUpperCase())
                // .saldoMonedas(dto.getSaldoMonedas() != null ? dto.getSaldoMonedas() : 0)
                .rut(dto.getRut())
                .telefono(dto.getTelefono())
                .descripcion(dto.getDescripcion())
                .privacidadDatos(dto.getPrivacidadDatos())
                .build();

        return userRepository.save(user);
    }

    public User registerClient(User dto) {
        dto.setRol("CLIENTE");

        return addUser(dto);
    }

    public List<User> viewAllUsers() {
        return userRepository.findAll();
    }

    public List<User> searchUsers(String query) {
        if (query == null || query.isBlank()) {
            return List.of();
        }
        String normalized = query.toLowerCase();
        return userRepository.findAll().stream()
                .filter(user -> (user.getNombre() != null && user.getNombre().toLowerCase().contains(normalized))
                        || (user.getApellido() != null && user.getApellido().toLowerCase().contains(normalized))
                        || (user.getEmail() != null && user.getEmail().toLowerCase().contains(normalized))
                        || (user.getRut() != null && user.getRut().toLowerCase().contains(normalized)))
                .toList();
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
        if (dto.getApellido() != null)
            user.setApellido(dto.getApellido());
        if (dto.getEmail() != null) {
            if (!dto.getEmail().equals(user.getEmail()) && userRepository.existsByEmail(dto.getEmail())) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Email ya en uso");
            }
            user.setEmail(dto.getEmail());
        }

        if (dto.getRut() != null) {
            if (!dto.getRut().equals(user.getRut()) && userRepository.existsByRut(dto.getRut())) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Rut ya en uso");
            }
            user.setRut(dto.getRut());
        }

        if (dto.getTelefono() != null) {
            user.setTelefono(dto.getTelefono());
        }

        if (dto.getDescripcion() != null) {
            user.setDescripcion(dto.getDescripcion());
        }

        if (dto.getPrivacidadDatos() != null) {
            user.setPrivacidadDatos(dto.getPrivacidadDatos());
        }

        if (dto.getRol() != null && !dto.getRol().isBlank()) {

            String rol = dto.getRol().toUpperCase();

            if (!rol.equals("ADMIN")
                    && !rol.equals("CLIENTE")
                    && !rol.equals("ORGANIZACION")) {

                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Rol inválido");
            }

            user.setRol(rol);
        }

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

    public User actualizarRol(Long usuarioId, String nuevoRol) {

        User user = userRepository.findById(usuarioId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Usuario no encontrado"));

        nuevoRol = nuevoRol.toUpperCase();

        if (!nuevoRol.equals("ADMIN")
                && !nuevoRol.equals("CLIENTE")
                && !nuevoRol.equals("ORGANIZACION")) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Rol inválido");
        }

        user.setRol(nuevoRol);

        return userRepository.save(user);

    }

}
