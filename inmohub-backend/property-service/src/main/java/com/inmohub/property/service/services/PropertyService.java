package com.inmohub.property.service.services;

import com.inmohub.property.service.clients.AuthClient;
import com.inmohub.property.service.dtos.PropertyCreateDto;
import com.inmohub.property.service.dtos.PropertyDto;
import com.inmohub.property.service.dtos.UserResponseDto;
import com.inmohub.property.service.exceptions.ResourceNotFoundException;
import com.inmohub.property.service.exceptions.UserNotActiveException;
import com.inmohub.property.service.mappers.IPropertyMapper;
import com.inmohub.property.service.models.Property;
import com.inmohub.property.service.repositories.IPropertyRepository;
import feign.FeignException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;


/**
 * Servicio de lógica de negocio para la gestión de propiedades inmobiliarias.
 *
 * Esta clase actúa como intermediario entre el controlador y la capa de persistencia.
 * Su responsabilidad principal es orquestar las operaciones CRUD sobre los inmuebles,
 * incluyendo la validación cruzada con el microservicio de autenticación (Auth-Service)
 * para asegurar la integridad de los datos del propietario.
 */
@Service
@AllArgsConstructor
@Slf4j
public class PropertyService {
    private final IPropertyRepository repository;
    private final IPropertyMapper mapper;
    private final AuthClient client; // Cliente Feign para comunicación síncrona con Auth-Service


    /**
     * Crea y persiste una nueva propiedad en la base de datos tras validar al propietario.
     *
     * Flujo de validación distribuida:
     * <ol>
     * <li>El servicio recibe la petición de creación con el ID del propietario ("ownerId").</li>
     * <li>Realiza una llamada HTTP síncrona, vía Feign Client, al microservicio {@code auth-service}.</li>
     * <li>Verifica si el usuario existe y si su estado es {@code ACTIVE}.</li>
     * <li>Si el usuario no está activo, se bloquea la operación lanzando {@link UserNotActiveException}.</li>
     * <li>Si el usuario no existe (404), se captura la excepción y se loguea una advertencia, permitiendo la creación (según reglas de negocio actuales).</li>
     * </ol>
     *
     * @param createDTO DTO con la información del inmueble a crear.
     * @return {@link PropertyDto} con los datos de la propiedad persistida, incluyendo su ID y fechas de auditoría.
     * @throws UserNotActiveException Si el propietario existe pero su cuenta no está activa.
     */
    public PropertyDto createProperty(PropertyCreateDto createDTO) {

        try {
            // Si el usuario no existe, feing lanza una excepcion 404
            UserResponseDto user = client.getUserById(createDTO.ownerId());

            if(!"ACTIVE".equals(user.status())) {
                throw new UserNotActiveException("El propietario no está activo y no puede publicar propiedades.");
            }
        }catch (FeignException.FeignClientException.NotFound e) {
            log.warn("Usuario con ID: {} no existe: {}", createDTO.ownerId(), e);
        }


        Property property = mapper.toEntity(createDTO);
        return mapper.toDTO(repository.save(property));
    }

    /**
     * Recupera el listado completo de propiedades registradas en el sistema.
     *
     * @return Una lista de objetos {@link PropertyDto} con la información de todos los inmuebles.
     */
    public List<PropertyDto> getAllProperties() {
            return repository.findAll().stream()
                .map(mapper::toDTO)
                .toList();
    }

    /**
     * Busca una propiedad específica por su identificador único (UUID).
     *
     * @param uuid El identificador único de la propiedad a buscar.
     * @return {@link PropertyDto} con los detalles del inmueble encontrado.
     * @throws ResourceNotFoundException Si no existe ninguna propiedad con el ID proporcionado en la base de datos.
     */
    public PropertyDto getPropertyById(UUID uuid) {
        return repository.findById(uuid)
                .map(mapper::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Propiedad no encontrada."));
    }

    /**
     * Filtra y recupera todas las propiedades asociadas a un propietario específico.
     *
     * @param ownerId El UUID del propietario (usuario) del cual se quieren listar los inmuebles.
     * @return Una lista de {@link PropertyDto} pertenecientes a ese propietario. Puede estar vacía si no tiene inmuebles.
     */
    public List<PropertyDto> findByOwnerId(UUID ownerId) {
        return repository.findByOwnerId(ownerId).stream()
                .map(mapper::toDTO)
                .toList();
    }

    /**
     * Elimina una propiedad de la base de datos.
     *
     * @param id El identificador único de la propiedad a eliminar.
     * @return {@code true} si la propiedad existía y fue eliminada correctamente,
     * {@code false} si la propiedad no existía y no se realizó ninguna acción.
     */
    public boolean deleteById(UUID id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return true;
        }
        return false;
    }
}
