package com.inmohub.auth.service;

import com.inmohub.auth.service.dtos.*;
import com.inmohub.auth.service.mappers.UserMapper;
import com.inmohub.auth.service.models.User;
import com.inmohub.auth.service.models.enums.UserStatus;
import com.inmohub.auth.service.repositories.IUserRepository;
import com.inmohub.auth.service.services.AuthService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests adicionales para {@link AuthService}.
 * Complementa los tests basicos con cobertura de operaciones de consulta:
 * listar todos los usuarios, verificacion de existencia por email/username
 * y busqueda por rol.
 *
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService - Tests adicionales")
class AuthServiceTest {

    @Mock
    private IUserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private AuthService authService;

    private final UUID userId = UUID.randomUUID();
    private final String email = "pepe.montana@gmail.com";
    private final String username = "pepemontana";

    @Test
    @DisplayName("getAllUsers debe retornar lista de usuarios")
    void obtenerTodosLosUsuarios() {
        User user = new User();
        user.setId(userId);
        user.setEmail(email);
        UserDto dto = new UserDto(userId, username, email, "Pepe", "Montana",
                "600123456", Set.of("AGENT"), UserStatus.ACTIVE, LocalDateTime.now(), LocalDateTime.now());

        when(userRepository.findAll()).thenReturn(List.of(user));
        when(userMapper.toDTO(user)).thenReturn(dto);

        List<UserDto> result = authService.getAllUsers();

        assertEquals(1, result.size());
        assertEquals(email, result.get(0).email());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("existsByEmail debe retornar true si el email existe")
    void emailExisteTrue() {
        when(userRepository.existsByEmail(email)).thenReturn(true);

        assertTrue(authService.existsByEmail(email));
        verify(userRepository, times(1)).existsByEmail(email);
    }

    @Test
    @DisplayName("existsByEmail debe retornar false si el email no existe")
    void emailNoExisteFalse() {
        when(userRepository.existsByEmail("noexiste@gmail.com")).thenReturn(false);

        assertFalse(authService.existsByEmail("noexiste@gmail.com"));
    }

    @Test
    @DisplayName("existsByUsername debe retornar true si el username existe")
    void usernameExisteTrue() {
        when(userRepository.existsByUsername(username)).thenReturn(true);

        assertTrue(authService.existsByUsername(username));
    }

    @Test
    @DisplayName("existsByUsername debe retornar false si el username no existe")
    void usernameNoExisteFalse() {
        when(userRepository.existsByUsername("noexiste")).thenReturn(false);

        assertFalse(authService.existsByUsername("noexiste"));
    }

    @Test
    @DisplayName("getByRole debe retornar usuarios con el rol especificado")
    void obtenerPorRol() {
        User user = new User();
        user.setId(userId);
        UserDto dto = new UserDto(userId, username, email, "Pepe", "Montana",
                "600123456", Set.of("AGENT"), UserStatus.ACTIVE, LocalDateTime.now(), LocalDateTime.now());

        when(userRepository.findByRoles_Name("AGENT")).thenReturn(List.of(user));
        when(userMapper.toDTO(user)).thenReturn(dto);

        List<UserDto> result = authService.getByRole("AGENT");

        assertEquals(1, result.size());
        verify(userRepository, times(1)).findByRoles_Name("AGENT");
    }
}
