package com.petone.mascotas.ms_mascotas.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "mascotas")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Pet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String nombre;
    private String raza;
    private String color;
    private String tamano; // Evitamos la 'ñ' por estándar de código
    private String estado;
    private String descripcion;
    private Long usuarioId; // Relación con el microservicio de usuarios

    @Column(length = 10000)
    private String fotoUrl;
}