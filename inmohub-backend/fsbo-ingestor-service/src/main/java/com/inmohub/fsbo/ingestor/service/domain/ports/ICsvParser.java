package com.inmohub.fsbo.ingestor.service.domain.ports;

import com.inmohub.fsbo.ingestor.service.domain.abstractions.Result;
import com.inmohub.fsbo.ingestor.service.domain.models.FsboBatch;
import com.inmohub.fsbo.ingestor.service.domain.models.OwnerDetails;

import java.io.InputStream;
import java.util.UUID;

/**
 * Puerto de entrada para el parseo de archivos CSV con datos de propiedades FSBO.
 */
public interface ICsvParser {
    Result<FsboBatch, String> parse(InputStream fileStream, OwnerDetails ownerDetails);
}
