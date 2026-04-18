package com.inmohub.property.service.mappers;

import com.inmohub.property.service.dtos.PropertyCreateDto;
import com.inmohub.property.service.dtos.PropertyDto;
import com.inmohub.property.service.models.Property;
import com.inmohub.property.service.models.enums.PropertyStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", imports = PropertyStatus.class)
public interface IPropertyMapper {
    /**
     * Realiza la conversión de entidad "Property" a DTO "PropertyDTO".
     *
     * @param property Entidad JPA.
     * @return objeto DTO PropertyDTO.
     */
    PropertyDto toDTO(Property property);

    /**
     * Convierte DTO de creación a Entidad.
     * Configura por defecto el estado como AVAILABLE.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "status", expression = "java(PropertyStatus.AVAILABLE)") // Por defecto se le asigna disponible
    Property toEntity(PropertyCreateDto createDTO);
}
