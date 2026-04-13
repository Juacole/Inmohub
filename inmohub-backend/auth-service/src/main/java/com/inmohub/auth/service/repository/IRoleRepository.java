package com.inmohub.auth.service.repository;

import com.inmohub.auth.service.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface IRoleRepository extends JpaRepository<Role, UUID> {
    Optional<Role> findByName(String name);
}