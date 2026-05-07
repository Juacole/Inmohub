package com.inmohub.property.service.repositories;

import com.inmohub.property.service.models.Property;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface IPropertyRepository extends JpaRepository<Property, UUID> {

    List<Property> findByOwnerId(UUID ownerId);

    @EntityGraph(attributePaths = {"photos"}) // Carga eager para las fotos
    Page<Property> findAll(Pageable pageable);
}
