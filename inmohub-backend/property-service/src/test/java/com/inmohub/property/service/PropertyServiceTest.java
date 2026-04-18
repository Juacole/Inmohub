package com.inmohub.property.service;

import com.inmohub.property.service.clients.AuthClient;
import com.inmohub.property.service.dtos.PropertyCreateDto;
import com.inmohub.property.service.dtos.PropertyDto;
import com.inmohub.property.service.dtos.UserResponseDto;
import com.inmohub.property.service.exceptions.ResourceNotFoundException;
import com.inmohub.property.service.exceptions.UserNotActiveException;
import com.inmohub.property.service.mappers.IPropertyMapper;
import com.inmohub.property.service.models.Property;
import com.inmohub.property.service.models.enums.PropertyStatus;
import com.inmohub.property.service.repositories.IPropertyRepository;
import com.inmohub.property.service.services.PropertyService;
import feign.FeignException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PropertyServiceTest {

    @Mock
    private IPropertyRepository repository;

    @Mock
    private IPropertyMapper mapper;

    @Mock
    private AuthClient authClient;

    @InjectMocks
    private PropertyService propertyService;

    private PropertyCreateDto createDTO;
    private Property mockEntity;
    private PropertyDto mockDTO;
    private UUID ownerId;
    private UUID propertyId;

    @BeforeEach
    void setUp() {
        ownerId = UUID.randomUUID();
        propertyId = UUID.randomUUID();

        createDTO = new PropertyCreateDto(
                "Chalet en Madrid", "Amplio chalet con piscina",
                new BigDecimal("450000.00"), 250.5, "Calle Mayor 123", ownerId
        );

        mockEntity = new Property();
        mockEntity.setId(propertyId);
        mockEntity.setTitle("Chalet en Madrid");
        mockEntity.setOwnerId(ownerId);
        mockEntity.setStatus(PropertyStatus.AVAILABLE);

        mockDTO = new PropertyDto(
                propertyId, "Chalet en Madrid", "Amplio chalet con piscina",
                new BigDecimal("450000.00"), 250.5, "Calle Mayor 123",
                PropertyStatus.AVAILABLE, ownerId, LocalDateTime.now(), LocalDateTime.now()
        );
    }

    @Test
    @DisplayName("Creación exitosa con usuario ACTIVE")
    void createProperty_Success_WhenUserIsActive() {
        UserResponseDto activeUser = new UserResponseDto(ownerId, "pepe@gmail.com", "AGENT", "ACTIVE");

        when(authClient.getUserById(ownerId)).thenReturn(activeUser);
        when(mapper.toEntity(createDTO)).thenReturn(mockEntity);
        when(repository.save(any(Property.class))).thenReturn(mockEntity);
        when(mapper.toDTO(mockEntity)).thenReturn(mockDTO);

        PropertyDto result = propertyService.createProperty(createDTO);

        assertNotNull(result);
        assertEquals(PropertyStatus.AVAILABLE, result.status());
        verify(authClient, times(1)).getUserById(ownerId);
        verify(repository, times(1)).save(mockEntity);
    }

    @Test
    @DisplayName("Camino 2: Falla la creación porque el usuario no es ACTIVE")
    void createProperty_ThrowsException_WhenUserIsNotActive() {
        UserResponseDto inactiveUser = new UserResponseDto(ownerId, "pepe@gmail.com", "AGENT", "BLOCKED");
        when(authClient.getUserById(ownerId)).thenReturn(inactiveUser);

        UserNotActiveException exception = assertThrows(UserNotActiveException.class, () -> {
            propertyService.createProperty(createDTO);
        });

        assertEquals("El propietario no está activo y no puede publicar propiedades.", exception.getMessage());
        verify(authClient, times(1)).getUserById(ownerId);
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("Camino 1: Permite creación con advertencia si el usuario no se encuentra (404)")
    void createProperty_SavesWithWarning_WhenUserNotFound() {
        FeignException.FeignClientException.NotFound notFoundException =
                mock(FeignException.FeignClientException.NotFound.class);

        when(authClient.getUserById(ownerId)).thenThrow(notFoundException);
        when(mapper.toEntity(createDTO)).thenReturn(mockEntity);
        when(repository.save(any(Property.class))).thenReturn(mockEntity);
        when(mapper.toDTO(mockEntity)).thenReturn(mockDTO);

        PropertyDto result = propertyService.createProperty(createDTO);

        assertNotNull(result);
        verify(repository, times(1)).save(mockEntity);
    }

    @Test
    @DisplayName("Buscar propiedad por ID lanza ResourceNotFoundException si no existe")
    void getPropertyById_NotFound_ThrowsException() {
        when(repository.findById(propertyId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            propertyService.getPropertyById(propertyId);
        });

        assertEquals("Propiedad no encontrada.", exception.getMessage());
        verify(repository, times(1)).findById(propertyId);
    }

    @Test
    @DisplayName("Eliminar por ID devuelve false si el recurso no existe")
    void deleteById_ReturnsFalse_WhenNotExists() {
        when(repository.existsById(propertyId)).thenReturn(false);

        boolean result = propertyService.deleteById(propertyId);

        assertFalse(result);
        verify(repository, times(1)).existsById(propertyId);
        verify(repository, never()).deleteById(any());
    }
}