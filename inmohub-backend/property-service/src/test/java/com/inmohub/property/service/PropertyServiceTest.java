package com.inmohub.property.service;

import com.inmohub.property.service.dtos.*;
import com.inmohub.property.service.exceptions.ResourceNotFoundException;
import com.inmohub.property.service.mappers.IPropertyMapper;
import com.inmohub.property.service.messaging.KafkaPropertyEventPublisher;
import com.inmohub.property.service.models.Property;
import com.inmohub.property.service.models.PropertyFeature;
import com.inmohub.property.service.models.enums.PropertyStatus;
import com.inmohub.property.service.repositories.IPropertyRepository;
import com.inmohub.property.service.services.PropertyService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests adicionales para {@link PropertyService}.
 * Complementa los tests basicos con cobertura de consulta de propiedades
 * (findByOwnerId, getAll, isOwner), actualizacion parcial (patchProperty),
 * eliminacion por owner y manejo de features.
 *
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("PropertyService - Tests adicionales")
class PropertyServiceTest {

    @Mock
    private IPropertyRepository repository;

    @Mock
    private IPropertyMapper mapper;

    @Mock
    private KafkaPropertyEventPublisher propertyEventPublisher;

    @InjectMocks
    private PropertyService propertyService;

    private final UUID ownerId = UUID.randomUUID();
    private final UUID propertyId = UUID.randomUUID();

    @Test
    @DisplayName("findByOwnerId debe retornar propiedades del propietario")
    void buscarPorOwnerId() {
        Property property = new Property();
        property.setId(propertyId);
        property.setOwnerId(ownerId);
        PropertyDto dto = new PropertyDto(propertyId, "Chalet", "Desc", new BigDecimal("100"),
                100.0, "Calle", "Madrid", PropertyStatus.AVAILABLE, ownerId,
                List.of(), List.of(), LocalDateTime.now(), LocalDateTime.now());

        when(repository.findByOwnerId(ownerId)).thenReturn(List.of(property));
        when(mapper.toDto(property)).thenReturn(dto);

        List<PropertyDto> result = propertyService.findByOwnerId(ownerId);

        assertEquals(1, result.size());
        assertEquals(propertyId, result.get(0).id());
        verify(repository, times(1)).findByOwnerId(ownerId);
    }

    @Test
    @DisplayName("getAllProperties debe retornar todas las propiedades")
    void obtenerTodasLasPropiedades() {
        Property property = new Property();
        property.setId(propertyId);
        PropertyDto dto = new PropertyDto(propertyId, "Chalet", "Desc", new BigDecimal("100"),
                100.0, "Calle", "Madrid", PropertyStatus.AVAILABLE, ownerId,
                List.of(), List.of(), LocalDateTime.now(), LocalDateTime.now());

        when(repository.findAll()).thenReturn(List.of(property));
        when(mapper.toDto(property)).thenReturn(dto);

        List<PropertyDto> result = propertyService.getAllProperties();

        assertEquals(1, result.size());
        verify(repository, times(1)).findAll();
    }

    @Test
    @DisplayName("deleteById debe retornar true si la propiedad existe")
    void eliminarPropiedadExistente() {
        when(repository.existsById(propertyId)).thenReturn(true);

        boolean result = propertyService.deleteById(propertyId);

        assertTrue(result);
        verify(repository, times(1)).deleteById(propertyId);
    }

    @Test
    @DisplayName("isOwner debe retornar true si el usuario es el propietario")
    void esPropietarioTrue() {
        Property property = new Property();
        property.setOwnerId(ownerId);
        when(repository.findById(propertyId)).thenReturn(Optional.of(property));

        boolean result = propertyService.isOwner(propertyId, ownerId.toString());

        assertTrue(result);
    }

    @Test
    @DisplayName("isOwner debe retornar false si el usuario no es el propietario")
    void noEsPropietarioFalse() {
        UUID otroOwner = UUID.randomUUID();
        Property property = new Property();
        property.setOwnerId(ownerId);
        when(repository.findById(propertyId)).thenReturn(Optional.of(property));

        boolean result = propertyService.isOwner(propertyId, otroOwner.toString());

        assertFalse(result);
    }

    @Test
    @DisplayName("isOwner debe retornar false si la propiedad no existe")
    void esPropietarioPropiedadNoExiste() {
        when(repository.findById(propertyId)).thenReturn(Optional.empty());

        boolean result = propertyService.isOwner(propertyId, ownerId.toString());

        assertFalse(result);
    }

    @Test
    @DisplayName("patchProperty debe actualizar campos no nulos")
    void actualizarPropiedadParcialmente() {
        Property property = new Property();
        property.setId(propertyId);
        property.setTitle("Original");
        property.setDescription("Desc original");
        property.setPrice(new BigDecimal("100"));

        PropertyPatchDto patchDto = new PropertyPatchDto("Nuevo titulo", null, new BigDecimal("200"),
                null, null, null, null, null, null, null);

        PropertyDto dto = new PropertyDto(propertyId, "Nuevo titulo", "Desc original", new BigDecimal("200"),
                100.0, "Calle", "Madrid", PropertyStatus.AVAILABLE, ownerId,
                List.of(), List.of(), LocalDateTime.now(), LocalDateTime.now());

        when(repository.findById(propertyId)).thenReturn(Optional.of(property));
        when(repository.save(any(Property.class))).thenReturn(property);
        when(mapper.toDto(any(Property.class))).thenReturn(dto);

        PropertyDto result = propertyService.patchProperty(propertyId, patchDto);

        assertNotNull(result);
        assertEquals("Nuevo titulo", result.title());
        assertEquals(new BigDecimal("200"), result.price());
        verify(repository, times(1)).save(property);
    }

    @Test
    @DisplayName("patchProperty debe lanzar excepcion si la propiedad no existe")
    void actualizarPropiedadInexistente() {
        PropertyPatchDto patchDto = new PropertyPatchDto("Nuevo", null, null,
                null, null, null, null, null, null, null);

        when(repository.findById(propertyId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            propertyService.patchProperty(propertyId, patchDto);
        });
    }

    @Test
    @DisplayName("deleteByOwnerId debe eliminar propiedades y publicar eventos")
    void eliminarPorOwnerId() {
        Property property = new Property();
        property.setId(propertyId);
        property.setOwnerId(ownerId);

        when(repository.findByOwnerId(ownerId)).thenReturn(List.of(property));

        propertyService.deleteByOwnerId(ownerId);

        verify(propertyEventPublisher, times(1)).publishPropertyDeleted(any());
        verify(repository, times(1)).delete(property);
    }

    @Test
    @DisplayName("patchProperty debe reemplazar features si se proporcionan")
    void actualizarFeatures() {
        Property property = new Property();
        property.setId(propertyId);
        PropertyFeature existingFeature = new PropertyFeature();
        property.getFeatures().add(existingFeature);

        PropertyFeatureDto newFeature = new PropertyFeatureDto("Piscina", "Si");
        PropertyPatchDto patchDto = new PropertyPatchDto(null, null, null,
                null, null, null, null, null, null, List.of(newFeature));

        PropertyDto dto = new PropertyDto(propertyId, "Titulo", "Desc", new BigDecimal("100"),
                100.0, "Calle", "Madrid", PropertyStatus.AVAILABLE, ownerId,
                List.of(), List.of(), LocalDateTime.now(), LocalDateTime.now());

        when(repository.findById(propertyId)).thenReturn(Optional.of(property));
        when(repository.save(property)).thenReturn(property);
        when(mapper.toDto(property)).thenReturn(dto);

        PropertyDto result = propertyService.patchProperty(propertyId, patchDto);

        assertNotNull(result);
        assertEquals(1, property.getFeatures().size());
        assertEquals("Piscina", property.getFeatures().get(0).getFeatureName());
    }
}
