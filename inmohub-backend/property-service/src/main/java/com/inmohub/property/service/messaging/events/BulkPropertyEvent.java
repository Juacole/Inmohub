package com.inmohub.property.service.messaging.events;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.inmohub.property.service.messaging.dtos.PropertyNodeDto;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record BulkPropertyEvent(
        String ownerId,
        List<PropertyNodeDto> properties
) {}