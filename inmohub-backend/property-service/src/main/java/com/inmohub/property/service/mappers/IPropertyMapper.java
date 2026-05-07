package com.inmohub.property.service.mappers;

import com.inmohub.property.service.dtos.PropertyCreateDto;
import com.inmohub.property.service.dtos.PropertyDto;
import com.inmohub.property.service.dtos.PropertySummaryDto;
import com.inmohub.property.service.models.Property;
import com.inmohub.property.service.models.PropertyPhoto;
import com.inmohub.property.service.models.enums.PropertyStatus;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.Collections;
import java.util.List;

@Mapper(componentModel = "spring", imports = PropertyStatus.class)
public interface IPropertyMapper {

    PropertyDto toDto(Property property);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "status", expression = "java(PropertyStatus.AVAILABLE)")
    Property toEntity(PropertyCreateDto createDTO);

    @AfterMapping
    default void linkFeatures(@MappingTarget Property property) {
        if (property.getFeatures() != null) {
            property.getFeatures().forEach(feature -> feature.setProperty(property));
        }
    }

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "title", ignore = true)
    @Mapping(target = "price", ignore = true)
    @Mapping(target = "city", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "primaryPhotoUrl", ignore = true)
    PropertySummaryDto toSummaryDto(Property property);

    @AfterMapping
    default void populateSummaryDto(@MappingTarget PropertySummaryDto summaryDto, Property property) {
        List<PropertyPhoto> photos = property != null ? property.getPhotos() : Collections.emptyList();
        String primaryPhotoUrl = extractPrimaryPhotoUrl(photos);
        String status = property != null ? property.getStatus().name() : null;

        summaryDto.setId(property.getId());
        summaryDto.setTitle(property.getTitle());
        summaryDto.setPrice(property.getPrice());
        summaryDto.setCity(property.getCity());
        summaryDto.setStatus(status);
        summaryDto.setPrimaryPhotoUrl(primaryPhotoUrl);
    }

    default String extractPrimaryPhotoUrl(List<PropertyPhoto> photos) {
        if (photos == null || photos.isEmpty()) {
            return null;
        }
        return photos.stream()
                .filter(PropertyPhoto::getIsPrimary)
                .findFirst()
                .orElse(photos.get(0))
                .getPhotoUrl();
    }
}