package com.inmohub.property.service.messaging.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PropertyNodeDto(
        UUID ownerId,
        String title,
        String description,
        BigDecimal price,
        Double areaM2,
        String address,
        String city,
        String state,
        String country,
        String status,
        List<FeatureNodeDto> features
) {}