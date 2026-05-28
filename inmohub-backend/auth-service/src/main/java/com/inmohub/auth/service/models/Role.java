package com.inmohub.auth.service.models;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

/**
 * Entidad que representa un rol de la plataforma (ADMIN, AGENT, OWNER, CLIENT).
 * Define los permisos base que puede tener un usuario.
 */
@Entity
@Table(name = "roles")
@Data
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String name;

    private String description;
}