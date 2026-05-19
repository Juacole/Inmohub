package com.inmohub.property.service.clients;

import com.inmohub.property.service.configs.FeignConfig;
import com.inmohub.property.service.dtos.UserResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

/**
 * Cliente Feign para la comunicación síncrona con el Auth-Service.
 *
 * Esta interfaz actúa como un proxy. Cuando llamamos a sus métodos, Spring Cloud OpenFeign
 * genera una petición HTTP real hacia el servicio "auth-service" registrado en Eureka.
 */
@FeignClient(name = "auth-service", configuration = FeignConfig.class) // Nombre del servicio en Eureka al que queremos llamar
public interface AuthClient {

    /**
     * Consulta los datos de un usuario por su ID en el servicio remoto.
     * Utilizado para validar si el propietario existe y está activo antes de crear una propiedad.
     *
     * @param id UUID del usuario a buscar.
     * @return DTO con la información básica del usuario (UserResponse).
     */
    @GetMapping("/api/v1/auth/search-by-id/{id}")
    UserResponseDto getUserById(@PathVariable(name = "id") UUID id);
}
