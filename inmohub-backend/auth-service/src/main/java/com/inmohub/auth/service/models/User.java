package com.inmohub.auth.service.models;

import com.inmohub.auth.service.models.enums.UserStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name="users")
@Data
@EntityListeners(AuditingEntityListener.class)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    @NotBlank(message = "El username es obligatorio")
    private String username;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(nullable = false, unique = true)
    @NotBlank(message = "Email obligatorio")
    @Email(message = "El formato del email no es válido")
    private String email;

    @Column(name = "first_name", nullable = false)
    @NotBlank
    private String firstName;

    @Column(name = "last_name", nullable = false)
    @NotBlank
    private String lastName;

    @Pattern(
            regexp = "^[67]\\d{8}$",
            message = "El número de móvil debe empezar por 6 o 7 y tener 9 dígitos"
    )
    @NotNull
    private String phone;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus status;

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<RefreshToken> refreshTokens = new ArrayList<>();

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
