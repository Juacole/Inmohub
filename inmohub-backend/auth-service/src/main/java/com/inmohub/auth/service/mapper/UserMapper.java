package com.inmohub.auth.service.mapper;

import com.inmohub.auth.service.dto.UserCreateDto;
import com.inmohub.auth.service.dto.UserDto;
import com.inmohub.auth.service.model.Role;
import com.inmohub.auth.service.model.User;
import com.inmohub.auth.service.model.enums.UserStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * Interfaz de mapeo para la conversión entre Entidades (JPA) y DTOs.
 *
 * Utiliza la librería MapStruct para generar automáticamente el código de implementación
 * en tiempo de compilación, asegurando alto rendimiento y tipo seguro.
 */
@Mapper(componentModel = "spring", imports = UserStatus.class) // Importamos el Enum para usarlo en la expresion
public interface UserMapper {

    /**
     * Convierte la entidad User a UserDTO.
     * Oculta datos sensibles como la contraseña.
     *
     * @param user Entidad de usuario.
     * @return DTO con datos públicos.
     */
    @Mapping(target = "roles", source = "roles", qualifiedByName = "mapRolesToStrings")
    UserDto toDTO(User user);

    /**
     * Convierte el DTO de creación a una entidad User lista para persistir.
     *
     * Configuraciones:
     * <ul>
     * <li>Ignora 'id', 'createdAt', 'updatedAt' porque se generan automáticamente.</li>
     * <li>Establece el estado inicial como {@code ACTIVE}.</li>
     * </ul>
     *
     * @param userCreateDTO Datos recibidos del formulario de registro.
     * @return Entidad User.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "status", constant = "ACTIVE") // Asignamos status por defecto
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "passwordHash", ignore = true)
    User toEntity(UserCreateDto userCreateDTO);

    @Named("mapRolesToStrings")
    default Set<String> mapRolesToStrings(Set<Role> roles) {
        if (roles == null) return Collections.emptySet();

        return roles.stream()
                .map(Role::getName)
                .collect(Collectors.toSet());
    }
}
