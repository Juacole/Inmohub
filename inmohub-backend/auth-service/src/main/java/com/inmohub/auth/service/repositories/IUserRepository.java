package com.inmohub.auth.service.repositories;

import com.inmohub.auth.service.models.User;
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

    // Como se ha actualizado role a roles en la entidad, hay que respetarlo para que JPA genere la query correctamente
    List<User> findByRoles_Name(String role); // La query resultante implementa un INNER JOIN
}
