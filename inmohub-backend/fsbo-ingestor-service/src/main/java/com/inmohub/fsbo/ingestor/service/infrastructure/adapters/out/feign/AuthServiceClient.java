package com.inmohub.fsbo.ingestor.service.infrastructure.adapters.out.feign;

import com.inmohub.fsbo.ingestor.service.infrastructure.configs.FeignAuthInterceptor;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "auth-service", configuration = FeignAuthInterceptor.class)
public interface AuthServiceClient {

    @GetMapping("/api/v1/auth/search-by-id/{id}")
    UserProfileResponse getUserById(@PathVariable("id") UUID id);
}