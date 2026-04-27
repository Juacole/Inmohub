package com.inmohub.fsbo.ingestor.service.domain.ports;

import com.inmohub.fsbo.ingestor.service.domain.models.FsboBatch;

public interface IFsboRepository {
    void saveBatch(FsboBatch batch);
    boolean existsByEmailOrPhone(String email, String phone);
}
