package com.inmohub.auth.service.repository;

import com.inmohub.auth.service.model.User;
import com.inmohub.auth.service.model.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface IUserRepository extends JpaRepository<User, UUID> {
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
    Optional<User> findByEmail(String email);
    List<User> findByRole(UserRole role);
}
