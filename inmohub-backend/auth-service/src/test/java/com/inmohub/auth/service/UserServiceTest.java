package com.inmohub.auth.service;

import com.inmohub.auth.service.dto.UserCreateDto;
import com.inmohub.auth.service.dto.UserDto;
import com.inmohub.auth.service.exception.ResourceNotFoundException;
import com.inmohub.auth.service.mapper.UserMapper;
import com.inmohub.auth.service.model.User;
import com.inmohub.auth.service.model.enums.UserStatus;
import com.inmohub.auth.service.repository.IUserRepository;
import com.inmohub.auth.service.service.UserService;
import com.inmohub.auth.service.service.util.PasswordUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private IUserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    private User mockUser;
    private UserDto mockUserDTO;

    private final String RAW_PASSWORD = "Password123";
    private final String HASHED_PASSWORD = PasswordUtil.hashPassword(RAW_PASSWORD);

    @BeforeEach
    void setUp() {
        UUID userId = UUID.randomUUID();

        mockUser = new User();
        mockUser.setId(userId);
        mockUser.setEmail("pepe.montana@gmail.com");
        mockUser.setPasswordHash(HASHED_PASSWORD);
        mockUser.setRoles(Set.of());
        mockUser.setStatus(UserStatus.ACTIVE);

        mockUserDTO = new UserDto(
                userId, "pepemontana", "pepe.montana@gmail.com", "Pepe", "Montana",
                "600123456", Set.of("AGENT"), UserStatus.ACTIVE, LocalDateTime.now(), LocalDateTime.now()
        );
    }

    @Test
    @DisplayName("Login exitoso con credenciales validas")
    void login_Success() {
        when(userRepository.findByEmail("pepe.montana@gmail.com")).thenReturn(Optional.of(mockUser));
        when(userMapper.toDTO(mockUser)).thenReturn(mockUserDTO);

        UserDto result = userService.login("pepe.montana@gmail.com", RAW_PASSWORD);

        assertNotNull(result);
        assertEquals("pepe.montana@gmail.com", result.email());
        verify(userRepository, times(1)).findByEmail("pepe.montana@gmail.com");
        verify(userMapper, times(1)).toDTO(mockUser);
    }

    @Test
    @DisplayName("Login falla porque el email no existe")
    void login_EmailNotFound_ThrowsException() {
        when(userRepository.findByEmail("noexiste@gmail.com")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.login("noexiste@gmail.com", RAW_PASSWORD);
        });

        assertEquals("Usuario no encontrado", exception.getMessage());
        verify(userRepository, times(1)).findByEmail("noexiste@gmail.com");
        verify(userMapper, never()).toDTO(any());
    }

    @Test
    @DisplayName("Camino 3: Login falla por contraseña incorrecta")
    void login_InvalidPassword_ThrowsException() {

        when(userRepository.findByEmail("pepe.montana@gmail.com")).thenReturn(Optional.of(mockUser));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.login("pepe.montana@gmail.com", "ClaveErronea");
        });

        assertEquals("Credenciales incorrectas", exception.getMessage());
        verify(userRepository, times(1)).findByEmail("pepe.montana@gmail.com");
        verify(userMapper, never()).toDTO(any());
    }

    @Test
    @DisplayName("Crear usuario encripta correctamente la contraseña")
    void createUser_Success() {
        UserCreateDto createDTO = new UserCreateDto(
                "pepemontana", RAW_PASSWORD, "pepe.montana@gmail.com", "Pepe", "Montana", "600123456", Set.of("AGENT")
        );

        when(userMapper.toEntity(createDTO)).thenReturn(mockUser);
        when(userRepository.save(any(User.class))).thenReturn(mockUser);
        when(userMapper.toDTO(mockUser)).thenReturn(mockUserDTO);

        UserDto result = userService.createUser(createDTO);

        assertNotNull(result);
        verify(userMapper, times(1)).toEntity(createDTO);
        verify(userRepository, times(1)).save(mockUser);
        verify(userMapper, times(1)).toDTO(mockUser);
    }

    @Test
    @DisplayName("Buscar usuario por ID inexistente lanza ResourceNotFoundException")
    void getById_NotFound_ThrowsException() {
        UUID randomId = UUID.randomUUID();
        when(userRepository.findById(randomId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            userService.getById(randomId);
        });

        assertEquals("Usuario no encontrado.", exception.getMessage());
        verify(userRepository, times(1)).findById(randomId);
    }
}