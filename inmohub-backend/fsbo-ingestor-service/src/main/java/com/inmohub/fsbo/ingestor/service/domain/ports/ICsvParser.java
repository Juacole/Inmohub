package com.inmohub.fsbo.ingestor.service.domain.ports;

import com.inmohub.fsbo.ingestor.service.domain.abstractions.Result;
import com.inmohub.fsbo.ingestor.service.domain.models.FsboBatch;

import java.io.InputStream;

public interface ICsvParser {
    Result<FsboBatch, String> parse(InputStream fileStream);
}
