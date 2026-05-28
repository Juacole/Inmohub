package com.inmohub.auth.service.repositories;

import com.inmohub.auth.service.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repositorio de acceso a datos para la entidad Role.
 * Expone busqueda de roles por nombre.
 */
@Repository
public interface IRoleRepository extends JpaRepository<Role, UUID> {
    Optional<Role> findByName(String name);
}