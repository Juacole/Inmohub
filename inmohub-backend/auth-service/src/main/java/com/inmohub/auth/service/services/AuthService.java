package com.inmohub.auth.service.services;

import com.inmohub.auth.service.dtos.AuthResponseDto;
import com.inmohub.auth.service.dtos.UpdateUserProfileRequest;
import com.inmohub.auth.service.dtos.UserCreateDto;
import com.inmohub.auth.service.dtos.UserDto;
import com.inmohub.auth.service.messaging.events.UserDeletedEvent;
import com.inmohub.auth.service.messaging.KafkaUserEventPublisher;
import com.inmohub.auth.service.exceptions.ResourceNotFoundException;
import com.inmohub.auth.service.mappers.UserMapper;
import com.inmohub.auth.service.models.RefreshToken;
import com.inmohub.auth.service.models.Role;
import com.inmohub.auth.service.models.User;
import com.inmohub.auth.service.models.enums.UserStatus;
import com.inmohub.auth.service.repositories.IRoleRepository;
import com.inmohub.auth.service.repositories.IUserRepository;
import com.inmohub.auth.service.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Servicio de lógica de negocio para la gestión de usuarios.
 * Encargado de la orquestación entre el controlador, el repositorio y las utilidades.
 */
@Service
@RequiredArgsConstructor
public class AuthService {
    private final IUserRepository repository;
    private final UserMapper mapper;
    private final IRoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final KafkaUserEventPublisher userEventPublisher;

    /**
     * Crea y persiste un nuevo usuario en la base de datos.
     * Realiza el hashing de la contraseña antes de guardar.
     *
     * @param createDTO Datos de entrada validados.
     * @return UserDTO Datos del usuario ya persistido.
     */
    public UserDto createUser(UserCreateDto createDTO) {
        User user = mapper.toEntity(createDTO);

        user.setPasswordHash(passwordEncoder.encode(createDTO.password()));

        Set<Role> roles;
        if (createDTO.roles() != null && !createDTO.roles().isEmpty()) {
            roles = createDTO.roles().stream()
                    .map(roleName -> roleRepository.findByName(roleName)
                            .orElseThrow(
                                    () -> new ResourceNotFoundException("Rol " + roleName + " no encontrado en base de datos.")
                            )
                    )
                    .collect(Collectors.toSet());
        } else {
            Role defaultRole = roleRepository.findByName("CLIENT")
                    .orElseThrow(
                            () -> new ResourceNotFoundException("Rol CLIENT no encontrado en la base de datos.")
                    );
            roles = Set.of(defaultRole);
        }
        user.setRoles(roles);
        user.setStatus(UserStatus.ACTIVE);
        return mapper.toDTO(repository.save(user));
    }

    /**
     * Recupera todos los usuarios del sistema.
     * @return Lista de usuarios convertidos a DTO.
     */
    @Transactional(readOnly = true)
    public List<UserDto> getAllUsers() {
        return repository.findAll().stream()
                .map(mapper::toDTO)
                .toList();
    }

    /**
     * Busca un usuario por su identificador único (UUID).
     *
     * @param uuid ID del usuario.
     * @return UserDTO si se encuentra.
     * @throws ResourceNotFoundException si el usuario no existe.
     */
    @Transactional(readOnly = true)
    public UserDto getById(UUID uuid) {
        return repository.findById(uuid)
                .map(mapper::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado."));
    }

    /**
     * Verifica si un email ya existe en la base de datos.
     * @param email Email a comprobar.
     * @return true si existe, false en caso contrario.
     */
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return repository.existsByEmail(email);
    }

    /**
     * Verifica si un nombre de usuario ya existe.
     * @param username Nombre de usuario a comprobar.
     * @return true si existe, false en caso contrario.
     */
    @Transactional(readOnly = true)
    public boolean existsByUsername(String username) {
        return repository.existsByUsername(username);
    }

    /**
     * Elimina un usuario por su ID.
     *
     * @param id UUID del usuario a eliminar.
     * @return true si se eliminó, false si no existía.
     */
    public boolean deleteById(UUID id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            userEventPublisher.publishUserDeleted(UserDeletedEvent.of(id));
            return true;
        }
        return false;
    }

    /**
     * Actualiza los datos personales básicos del perfil de un usuario.
     * Solo los campos no nulos y no vacíos del request serán modificados.
     *
     * @param userId  UUID del usuario autenticado.
     * @param request Datos parciales a actualizar (firstName, lastName, phone).
     * @return UserDto con los datos actualizados.
     * @throws ResourceNotFoundException si el usuario no existe.
     */
    public UserDto updateProfile(UUID userId, UpdateUserProfileRequest request) {
        User user = repository.findById(userId)
                .orElseThrow(
                        () -> new ResourceNotFoundException("Usuario no encontrado.")
                );

        if (request.firstName() != null && !request.firstName().isBlank()) {
            user.setFirstName(request.firstName());
        }
        if (request.lastName() != null && !request.lastName().isBlank()) {
            user.setLastName(request.lastName());
        }
        if (request.phone() != null && !request.phone().isBlank()) {
            user.setPhone(request.phone());
        }

        return mapper.toDTO(repository.save(user));
    }

    /**
     * Realiza el proceso de login verificando email y contraseña.
     * Genera un token resultante si el login es existoso.
     *
     * @param email Email del usuario.
     * @param password Contraseña en texto plano introducida por el usuario.
     * @return Dto incluye nuevo JWT y Refresh Token.
     * @throws RuntimeException si el usuario no existe.
     */
    public AuthResponseDto login(String email, String password) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));

        User user = repository.findByEmail(email).orElseThrow(() -> new RuntimeException("Usuario no encontrado."));

        String jwt = jwtService.generateToken(user);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getId());

        return new AuthResponseDto(jwt, refreshToken.getToken());
    }

    @Transactional(readOnly = true)
    public AuthResponseDto refreshToken(String requestRefreshToken) {
        return refreshTokenService.findByToken(requestRefreshToken)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    String newAccessToken = jwtService.generateToken(user);
                    // Se devuelve un nuevo JWT y se mantiene el mismo refresh token
                    return new AuthResponseDto(newAccessToken, requestRefreshToken);
                })
                .orElseThrow(() -> new RuntimeException("Refresh token no encontrado."));
    }

    /**
     * Filtra usuarios por su rol en el sistema.
     *
     * @param userRole String con el nombre del rol (ej: "AGENT").
     * @return Lista de usuarios que tienen ese rol.
     * @throws IllegalArgumentException si el rol no existe en el Enum.
     */
    @Transactional(readOnly = true)
    public List<UserDto> getByRole(String userRole) {
        return repository.findByRoles_Name(userRole)
                .stream()
                .map(mapper::toDTO)
                .toList();
    }
}
