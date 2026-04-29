package com.inmohub.property.service.messaging.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record BulkPropertyEventDto(
        String ownerId,
        List<PropertyNodeDto> properties
) {}