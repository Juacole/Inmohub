package com.inmohub.property.service.messaging.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record FeatureNodeDto(
        String featureName,
        String featureValue
) {}