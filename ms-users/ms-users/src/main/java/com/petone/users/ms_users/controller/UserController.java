package com.petone.users.ms_users.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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
}
