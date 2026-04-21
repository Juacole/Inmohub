package com.inmohub.lead.service.infrastructure.adapters.in.web;

import com.inmohub.lead.service.application.dto.CreateLeadRequest;
import com.inmohub.lead.service.application.dto.LeadResponse;
import com.inmohub.lead.service.application.usecases.CreateLeadUseCase;
import com.inmohub.lead.service.domain.abstractions.Error;
import com.inmohub.lead.service.domain.abstractions.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/leads")
@RequiredArgsConstructor
@Tag(name = "Leads", description = "Gestión de clientes potenciales (Leads)")
public class LeadController {

    private final CreateLeadUseCase createLeadUseCase;

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crear un nuevo Lead", description = "Registra un interesado en una propiedad")
    public Result<LeadResponse, Error> createLead(@RequestBody CreateLeadRequest request) {
        return createLeadUseCase.execute(request);
    }
}