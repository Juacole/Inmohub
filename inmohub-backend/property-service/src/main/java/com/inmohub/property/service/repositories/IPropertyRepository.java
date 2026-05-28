package com.inmohub.property.service.repositories;

import com.inmohub.property.service.models.Property;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repositorio JPA para la entidad Property.
 * Expone operaciones de busqueda por propietario, paginacion con carga eager de fotos,
 * y soporte para filtros dinamicos mediante Specifications.
 */
@Repository
public interface IPropertyRepository extends JpaRepository<Property, UUID>, JpaSpecificationExecutor<Property> {

    List<Property> findByOwnerId(UUID ownerId);

    @EntityGraph(attributePaths = {"photos"}) // Carga eager para las fotos
    Page<Property> findAll(Pageable pageable);
}
