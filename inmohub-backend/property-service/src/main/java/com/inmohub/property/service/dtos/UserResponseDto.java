package com.inmohub.property.service.dtos;

import java.util.Set;
import java.util.UUID;

/**
 * DTO que transporta la informacion basica de un usuario obtenida desde el Auth-Service.
 * Se utiliza para validar el estado y roles del propietario antes de crear una propiedad.
 */
public record UserResponseDto(
        UUID id,
        String email,
        String name,
        String phone,
        Set<String> roles,
        String status
) {
}
