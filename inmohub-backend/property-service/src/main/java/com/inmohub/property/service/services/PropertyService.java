package com.inmohub.property.service.services;

import com.inmohub.property.service.clients.AuthClient;
import com.inmohub.property.service.dtos.PropertyCreateDto;
import com.inmohub.property.service.dtos.PropertyDto;
import com.inmohub.property.service.dtos.PropertyPatchDto;
import com.inmohub.property.service.dtos.PropertyPhotoDto;
import com.inmohub.property.service.dtos.PropertySummaryDto;
import com.inmohub.property.service.dtos.UserResponseDto;
import com.inmohub.property.service.exceptions.ResourceNotFoundException;
import com.inmohub.property.service.exceptions.UserNotActiveException;
import com.inmohub.property.service.mappers.IPropertyMapper;
import com.inmohub.property.service.dtos.PropertySearchCriteria;
import com.inmohub.property.service.messaging.KafkaLeadEventPublisher;
import com.inmohub.property.service.messaging.KafkaPropertyEventPublisher;
import com.inmohub.property.service.messaging.events.BulkPropertyEvent;
import com.inmohub.property.service.messaging.events.PropertyDeletedEvent;
import com.inmohub.property.service.specifications.PropertySpecifications;
import com.inmohub.property.service.models.Property;
import com.inmohub.property.service.models.PropertyFeature;
import com.inmohub.property.service.models.PropertyPhoto;
import com.inmohub.property.service.models.enums.PropertyStatus;
import com.inmohub.property.service.repositories.IPropertyRepository;
import feign.FeignException;
import org.springframework.transaction.annotation.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
    private final IPropertyRepository propertyRepository;
    private final IPropertyMapper propertyMapper;
    private final AuthClient client;
    private final FirebaseStorageService firebaseService;
    private final KafkaLeadEventPublisher kafkaLeadEventPublisher;
    private final KafkaPropertyEventPublisher propertyEventPublisher;

    /**
     * Orquesta la creación y persistencia de un nuevo inmueble, integrando validación delegada,
     * almacenamiento multimedia en la nube y mapeo de relaciones estructurales.
     *
     * <p>Esta operación es completamente transaccional ({@link org.springframework.transaction.annotation.Transactional}).
     * Si ocurre algún fallo durante la persistencia en base de datos, se garantizará la consistencia de los datos locales.</p>
     *
     * <h3>Flujo de ejecución:</h3>
     * <ol>
     * <li><b>Validación Distribuida:</b> Realiza una llamada HTTP síncrona al microservicio {@code auth-service} vía Feign Client.
     * Verifica que el {@code ownerId} corresponda a un usuario existente y con estado {@code ACTIVE}.</li>
     * <li><b>Mapeo y Características:</b> Transforma el DTO a entidad y establece las relaciones bidireccionales
     * (1:N) con las características del inmueble ({@code PropertyFeature}).</li>
     * <li><b>Procesamiento Multimedia:</b> Itera sobre las imágenes adjuntas, subiéndolas a Firebase Storage.
     * La primera imagen de la lista se establece automáticamente como la foto principal del inmueble.</li>
     * <li><b>Persistencia:</b> Guarda la entidad raíz en cascada y retorna el estado final.</li>
     * </ol>
     *
     * @param dto     Objeto de transferencia con los metadatos principales del inmueble.
     * @param photos  Lista de archivos multipart que contienen las imágenes de la propiedad. Puede ser nula o vacía.
     * @param ownerId Identificador unívoco del propietario, inyectado desde el contexto de seguridad (Gateway/JWT).
     * @return {@link PropertyDto} con la información del inmueble persistido, incluyendo IDs generados, URLs de Firebase y fechas de auditoría.
     * @throws UserNotActiveException    Si el propietario existe en el ecosistema pero su cuenta está suspendida o inactiva.
     * @throws ResourceNotFoundException Si el {@code auth-service} devuelve un 404 (el usuario no existe).
     * @throws IOException               Si ocurre un error durante el procesamiento binario o la subida de archivos a Firebase Storage.
     */
    @Transactional
    public PropertyDto createProperty(PropertyCreateDto dto, List<MultipartFile> photos, UUID ownerId) throws IOException {
        UserResponseDto owner = validateOwnerStatus(ownerId);

        Property property = propertyMapper.toEntity(dto);
        property.setOwnerId(ownerId);
        property.setId(UUID.randomUUID());
        property.setStatus(PropertyStatus.AVAILABLE);

        if (dto.features() != null) {
            dto.features().forEach(f -> {
                PropertyFeature feature = new PropertyFeature();
                feature.setFeatureName(f.featureName());
                feature.setFeatureValue(f.featureValue());
                property.addFeature(feature);
            });
        }

        if (photos != null && !photos.isEmpty()) {
            for (int i = 0; i < photos.size(); i++) {
                String url = firebaseService.uploadPhoto(photos.get(i));
                PropertyPhoto photo = new PropertyPhoto();
                photo.setPhotoUrl(url);
                photo.setIsPrimary(i == 0);
                property.addPhoto(photo);
            }
        }

        PropertyDto savedProperty = propertyMapper.toDto(propertyRepository.save(property));
        log.info("Propiedad creada con éxito: ID {}", savedProperty.id());


        if (
                owner != null && owner.roles() != null &&
                owner.roles().stream()
                        .anyMatch(r -> r.equalsIgnoreCase("OWNER"))
        ) {
            try {
                kafkaLeadEventPublisher.publishIndividualPropertyLead(savedProperty.id(), owner);
            } catch (Exception e) {
                log.error("Error al publicar evento de lead para propiedad {}: {}", savedProperty.id(), e.getMessage());
            }
        } else {
            log.warn("El usuario {} no tiene el rol OWNER. Roles detectados: {}", owner.id(), owner.roles());
        }

        return savedProperty;
    }

    private UserResponseDto validateOwnerStatus(UUID ownerId) {
        try {
            UserResponseDto user = client.getUserById(ownerId);
            if (!"ACTIVE".equalsIgnoreCase(user.status())) {
                throw new UserNotActiveException("El usuario debe estar activo para publicar.");
            }
            return user;
        } catch (FeignException.NotFound e) {
            throw new ResourceNotFoundException("El propietario no existe en Auth-Service.");
        }
    }

    /**
     * Recupera el listado completo de propiedades registradas en el sistema.
     *
     * @return Una lista de objetos {@link PropertyDto} con la información de todos los inmuebles.
     */
    @Transactional(readOnly = true)
    public List<PropertyDto> getAllProperties() {
            return propertyRepository.findAll().stream()
                .map(propertyMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public Page<PropertySummaryDto> getPropertiesSummary(Pageable pageable) {
        return propertyRepository.findAll(pageable)
                .map(propertyMapper::toSummaryDto);
    }

    @Transactional(readOnly = true)
    public Page<PropertySummaryDto> searchProperties(PropertySearchCriteria criteria, Pageable pageable) {
        var spec = PropertySpecifications.buildSpecification(
                criteria.city(),
                criteria.minPrice(),
                criteria.maxPrice(),
                criteria.status()
        );
        return propertyRepository.findAll(spec, pageable)
                .map(propertyMapper::toSummaryDto);
    }

    /**
     * Busca una propiedad específica por su identificador único (UUID).
     *
     * @param uuid El identificador único de la propiedad a buscar.
     * @return {@link PropertyDto} con los detalles del inmueble encontrado.
     * @throws ResourceNotFoundException Si no existe ninguna propiedad con el ID proporcionado en la base de datos.
     */
    @Transactional(readOnly = true)
    public PropertyDto getPropertyById(UUID uuid) {
        return propertyRepository.findById(uuid)
                .map(propertyMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Propiedad no encontrada."));
    }

    /**
     * Filtra y recupera todas las propiedades asociadas a un propietario específico.
     *
     * @param ownerId El UUID del propietario (usuario) del cual se quieren listar los inmuebles.
     * @return Una lista de {@link PropertyDto} pertenecientes a ese propietario. Puede estar vacía si no tiene inmuebles.
     */
    @Transactional(readOnly = true)
    public List<PropertyDto> findByOwnerId(UUID ownerId) {
        return propertyRepository.findByOwnerId(ownerId).stream()
                .map(propertyMapper::toDto)
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
        if (propertyRepository.existsById(id)) {
            propertyRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Transactional
    public void deleteByOwnerId(UUID ownerId) {
        List<Property> properties = propertyRepository.findByOwnerId(ownerId);

        for (Property property : properties) {
            propertyEventPublisher.publishPropertyDeleted(PropertyDeletedEvent.of(property.getId()));
            propertyRepository.delete(property);
        }

        log.info("Eliminadas {} propiedades del ownerId={}", properties.size(), ownerId);
    }

    @Transactional(readOnly = true)
    public boolean isOwner(UUID propertyId, String userId) {
        return propertyRepository.findById(propertyId)
                .map(p -> p.getOwnerId().equals(UUID.fromString(userId)))
                .orElse(false);
    }

    @Transactional
    public PropertyDto patchProperty(UUID propertyId, PropertyPatchDto dto) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new ResourceNotFoundException("Propiedad no encontrada."));

        if (dto.title() != null && !dto.title().isBlank()) {
            property.setTitle(dto.title());
        }
        if (dto.description() != null && !dto.description().isBlank()) {
            property.setDescription(dto.description());
        }
        if (dto.price() != null) {
            property.setPrice(dto.price());
        }
        if (dto.areaM2() != null) {
            property.setAreaM2(dto.areaM2());
        }
        if (dto.address() != null && !dto.address().isBlank()) {
            property.setAddress(dto.address());
        }
        if (dto.city() != null && !dto.city().isBlank()) {
            property.setCity(dto.city());
        }
        if (dto.state() != null && !dto.state().isBlank()) {
            property.setState(dto.state());
        }
        if (dto.country() != null && !dto.country().isBlank()) {
            property.setCountry(dto.country());
        }
        if (dto.status() != null) {
            property.setStatus(dto.status());
        }

        if (dto.features() != null) {
            property.getFeatures().clear();
            dto.features().forEach(f -> {
                PropertyFeature feature = new PropertyFeature();
                feature.setFeatureName(f.featureName());
                feature.setFeatureValue(f.featureValue());
                property.addFeature(feature);
            });
        }

        PropertyDto patchedProperty = propertyMapper.toDto(propertyRepository.save(property));
        log.info("Propiedad actualizada parcialmente con éxito: ID {}", patchedProperty.id());
        return patchedProperty;
    }

    @Transactional
    public List<PropertyPhotoDto> addImages(UUID propertyId, List<MultipartFile> photos) throws IOException {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new ResourceNotFoundException("Propiedad no encontrada."));

        boolean firstPhotoIsPrimary = property.getPhotos().isEmpty();

        for (MultipartFile photoFile : photos) {
            String url = firebaseService.uploadPhoto(photoFile);
            PropertyPhoto photo = new PropertyPhoto();
            photo.setPhotoUrl(url);
            photo.setIsPrimary(firstPhotoIsPrimary);
            property.addPhoto(photo);
            firstPhotoIsPrimary = false;
        }

        propertyRepository.save(property);
        log.info("{} imágenes añadidas a la propiedad: ID {}", photos.size(), propertyId);

        return property.getPhotos().stream()
                .map(propertyMapper::toPhotoDto)
                .toList();
    }

    @Transactional
    public void processBulkProperties(BulkPropertyEvent eventDto) {
        log.info("Iniciando procesamiento de lote de propiedades para el owner: {}", eventDto.ownerId());

        if (eventDto.properties() == null || eventDto.properties().isEmpty()) {
            log.warn("El evento de bulk insert no contiene propiedades.");
            return;
        }

        List<Property> propertiesToSave = eventDto.properties().stream().map(node -> {
            Property property = new Property();
            property.setId(UUID.fromString(node.propertyId()));
            property.setOwnerId(UUID.fromString(node.ownerId()));
            property.setTitle(node.title());
            property.setDescription(node.description());
            property.setPrice(node.price());
            property.setAreaM2(node.areaM2());
            property.setAddress(node.address());
            property.setCity(node.city());
            property.setState(node.state());
            property.setCountry(node.country());
            property.setStatus(PropertyStatus.valueOf(node.status()));

            if (node.features() != null) {
                node.features().forEach(f -> {
                    PropertyFeature feature = new PropertyFeature();
                    feature.setFeatureName(f.featureName());
                    feature.setFeatureValue(f.featureValue());
                    property.addFeature(feature);
                });
            }
            return property;
        }).toList();

        propertyRepository.saveAll(propertiesToSave);
        log.info("Lote de {} propiedades guardado exitosamente.", propertiesToSave.size());
    }
}
