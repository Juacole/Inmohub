package com.inmohub.auth.service.services;

import com.inmohub.auth.service.dtos.UserCreateDto;
import com.inmohub.auth.service.dtos.UserDto;
import com.inmohub.auth.service.exceptions.ResourceNotFoundException;
import com.inmohub.auth.service.mappers.UserMapper;
import com.inmohub.auth.service.models.Role;
import com.inmohub.auth.service.models.User;
import com.inmohub.auth.service.models.enums.UserStatus;
import com.inmohub.auth.service.repositories.IRoleRepository;
import com.inmohub.auth.service.repositories.IUserRepository;
import com.inmohub.auth.service.services.util.PasswordUtil;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Servicio de lógica de negocio para la gestión de usuarios.
 * Encargado de la orquestación entre el controlador, el repositorio y las utilidades.
 */
@Service
@AllArgsConstructor
public class UserService {
    private final IUserRepository repository;
    private final UserMapper mapper;
    private final IRoleRepository roleRepository;

    /**
     * Crea y persiste un nuevo usuario en la base de datos.
     * Realiza el hashing de la contraseña antes de guardar.
     *
     * @param createDTO Datos de entrada validados.
     * @return UserDTO Datos del usuario ya persistido.
     */
    public UserDto createUser(UserCreateDto createDTO) {
        User user = mapper.toEntity(createDTO);

        // TODO: Reemplazar PasswordUtil manual por BCryptPasswordEncoder de Spring Security
        user.setPasswordHash(PasswordUtil.hashPassword(createDTO.password())); // Provisional

        // Asignación por defecto - provisional
        String defaultRoleName = (createDTO.roles() != null && !createDTO.roles().isEmpty())
                ? createDTO.roles().iterator().next()
                : "CLIENT";

        Role role = roleRepository.findByName(defaultRoleName)
                .orElseThrow(() -> new ResourceNotFoundException("Rol " + defaultRoleName + " no encontrado en la base de datos."));

        user.setRoles(Set.of(role));
        user.setStatus(UserStatus.ACTIVE);
        return mapper.toDTO(repository.save(user));
    }

    /**
     * Recupera todos los usuarios del sistema.
     * @return Lista de usuarios convertidos a DTO.
     */
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
    public boolean existsByEmail(String email) {
        return repository.existsByEmail(email);
    }

    /**
     * Verifica si un nombre de usuario ya existe.
     * @param username Nombre de usuario a comprobar.
     * @return true si existe, false en caso contrario.
     */
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
            return true;
        }
        return false;
    }

    /**
     * Realiza el proceso de login verificando email y contraseña.
     *
     * @param email Email del usuario.
     * @param password Contraseña en texto plano introducida por el usuario.
     * @return UserDTO si las credenciales son correctas.
     * @throws RuntimeException si el usuario no existe o la contraseña no coincide.
     */
    public UserDto login(String email, String password) {
        User user = repository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (!PasswordUtil.checkPassword(password, user.getPasswordHash())) {
            throw new RuntimeException("Credenciales incorrectas");
        }

        return mapper.toDTO(user);
    }

    /**
     * Filtra usuarios por su rol en el sistema.
     *
     * @param userRole String con el nombre del rol (ej: "AGENT").
     * @return Lista de usuarios que tienen ese rol.
     * @throws IllegalArgumentException si el rol no existe en el Enum.
     */
    public List<UserDto> getByRole(String userRole) {
        return repository.findByRoles_Name(userRole)
                .stream()
                .map(mapper::toDTO)
                .toList();
    }
}
