package com.inmohub.auth.service.mappers;

import com.inmohub.auth.service.dtos.UserCreateDto;
import com.inmohub.auth.service.dtos.UserDto;
import com.inmohub.auth.service.models.Role;
import com.inmohub.auth.service.models.User;
import com.inmohub.auth.service.models.enums.UserStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests para {@link UserMapper}.
 * Verifica el mapeo bidireccional entre entidades User y DTOs,
 * incluyendo la conversion de roles a Strings y el estado por defecto ACTIVE.
 */
@DisplayName("UserMapper")
class UserMapperTest {

    private final UserMapper userMapper = new UserMapperImpl();

    @Test
    @DisplayName("toDTO debe mapear roles a Set de Strings")
    void toDtoMapeaRolesCorrectamente() {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setUsername("pepemontana");
        user.setEmail("pepe@test.com");
        user.setFirstName("Pepe");
        user.setLastName("Montana");
        user.setPhone("600123456");
        user.setStatus(UserStatus.ACTIVE);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        Role role = new Role();
        role.setName("AGENT");
        user.setRoles(Set.of(role));

        UserDto dto = userMapper.toDTO(user);

        assertEquals("pepemontana", dto.username());
        assertTrue(dto.roles().contains("AGENT"));
    }

    @Test
    @DisplayName("toDTO debe manejar roles null")
    void toDtoRolesNull() {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setUsername("pepe");
        user.setEmail("pepe@test.com");
        user.setFirstName("Pepe");
        user.setLastName("Montana");
        user.setPhone("600123456");
        user.setStatus(UserStatus.ACTIVE);
        user.setRoles(null);

        UserDto dto = userMapper.toDTO(user);

        assertNotNull(dto.roles());
        assertTrue(dto.roles().isEmpty());
    }

    @Test
    @DisplayName("toEntity debe crear entidad con estado ACTIVE por defecto")
    void toEntityEstadoPorDefecto() {
        UserCreateDto createDto = new UserCreateDto("pepe", "password", "pepe@test.com",
                "Pepe", "Montana", "600123456", Set.of("AGENT"));

        User entity = userMapper.toEntity(createDto);

        assertNotNull(entity);
        assertNull(entity.getId());
        assertEquals(UserStatus.ACTIVE, entity.getStatus());
        assertEquals("pepe", entity.getUsername());
        assertEquals("pepe@test.com", entity.getEmail());
    }
}
