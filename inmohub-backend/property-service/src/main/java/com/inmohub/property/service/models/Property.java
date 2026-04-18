package com.inmohub.property.service.models;

import com.inmohub.property.service.models.enums.PropertyStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entidad JPA que representa la tabla "properties" en la base de datos PostgreSQL.
 */
@Entity
@Table(name = "properties")
@Data
@EntityListeners(AuditingEntityListener.class) // Activa la auditoría de fechas
public class Property {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank(message = "El titulo es obligatorio")
    @Size(min = 5, max = 100, message = "El titulo debe tener entre 5 y 100 caracteres")
    private String title;

    @NotBlank(message = "La descripcion es obligatorio")
    @Column(columnDefinition = "TEXT") // Para permitir textos largos
    private String description;

    @NotNull(message = "El precio es obligatorio")
    @Positive(message = "El precio debe ser mayor a cero")
    private BigDecimal price;

    @NotNull(message = "El área es obligatoria")
    @Positive(message = "El área debe ser positiva")
    @Column(name = "area_m2")
    private Double areaM2;

    @NotBlank(message = "La dirección es obligatoria")
    private String address;

    @NotNull(message = "El estado es obligatorio")
    @Enumerated(EnumType.STRING) // Para convertir el enum a texto
    private PropertyStatus status;

    // Almacenamos solo el ID. Para obtener datos del usuario
    // se consulta al Auth-Service en tiempo real.
    @NotNull(message = "El ID del propietario es obligatorio")
    @Column(name = "owner_id", nullable = false)
    private UUID ownerId;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
