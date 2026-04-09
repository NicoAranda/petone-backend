package com.petone.users.ms_users.controller;

import java.util.Collections;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.petone.users.ms_users.model.User;
import com.petone.users.ms_users.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class UserController {
    
    private final UserService userService;

    @PostMapping
    public ResponseEntity<User> registrar(@RequestBody User dto){
        User user = userService.addUser(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User dto){
        String token = userService.login(dto);
        return ResponseEntity.ok(Collections.singletonMap("token", token));
    }

    @GetMapping
    public ResponseEntity<List<User>> listarUsuarios(){
        List<User> users = userService.viewAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> mostrarUsuario(@PathVariable Long id){
        User user = userService.viewUserById(id);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> actualizarUsuario(@PathVariable Long id, @RequestBody User dto){
        User user = userService.updateUserById(id, dto);
        return ResponseEntity.ok(user);
    }

    @PatchMapping("/{id}/saldo")
    public ResponseEntity<User> actualizarSaldo(
        @PathVariable Long id, 
        @RequestParam Integer nuevaCantidad, 
        @RequestParam String adminRol
    ){
        User user = userService.updateSaldo(id, nuevaCantidad, adminRol);
        return ResponseEntity.ok(user);
    }

    @DeleteMapping("/{id}") 
    public ResponseEntity<Void> eliminarUsuario(@PathVariable Long id){
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
