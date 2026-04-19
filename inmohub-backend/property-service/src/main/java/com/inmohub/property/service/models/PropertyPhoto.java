package com.inmohub.property.service.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "property_photos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PropertyPhoto {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "property_id", nullable = false)
    private Property property;

    @Column(name = "photo_url", nullable = false)
    private String photoUrl;

    @Column(name = "is_primary", nullable = false)
    private Boolean isPrimary = false;
}
