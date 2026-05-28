package com.inmohub.property.service.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

/**
 * Entidad JPA que representa las caracteristicas adicionales de una propiedad (clave-valor).
 * Cada propiedad puede tener multiples caracteristicas asociadas.
 */
@Entity
@Table(name = "property_features")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PropertyFeature {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "property_id", nullable = false)
    private Property property;

    @Column(name = "feature_name", nullable = false)
    private String featureName;

    @Column(name = "feature_value", nullable = false)
    private String featureValue;
}
