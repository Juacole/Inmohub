package com.inmohub.property.service.repositories;

import com.inmohub.property.service.models.Property;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface IPropertyRepository extends JpaRepository<Property, UUID> {

    /**
     * Busca todas las propiedades pertenecientes a un propietario específico.
     * @param ownerId UUID del propietario.
     * @return Lista de propiedades.
     */
    List<Property> findByOwnerId(UUID ownerId);
}
