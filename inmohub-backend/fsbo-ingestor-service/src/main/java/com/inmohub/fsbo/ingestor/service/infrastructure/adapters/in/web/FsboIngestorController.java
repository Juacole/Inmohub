package com.inmohub.fsbo.ingestor.service.infrastructure.adapters.in.web;

import com.inmohub.fsbo.ingestor.service.application.dtos.FsboResponse;
import com.inmohub.fsbo.ingestor.service.application.usecases.IngestFsboFileUseCase;
import com.inmohub.fsbo.ingestor.service.domain.abstractions.Result;
import com.inmohub.fsbo.ingestor.service.domain.models.OwnerDetails;
import com.inmohub.fsbo.ingestor.service.infrastructure.adapters.out.feign.AuthServiceClient;
import com.inmohub.fsbo.ingestor.service.infrastructure.adapters.out.feign.UserProfileResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/fsbo")
@RequiredArgsConstructor
@Tag(name = "FSBO Ingestion", description = "Endpoints para la subida masiva de inmuebles por propietarios")
public class FsboIngestorController {

    private final IngestFsboFileUseCase useCase;
    private final AuthServiceClient authServiceClient;

    @PostMapping(value = "/properties/bulk", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
    @Operation(summary = "Sube un CSV con múltiples inmuebles vinculados al propietario autenticado")
    public ResponseEntity<?> uploadPropertiesBulk(@RequestPart("file") MultipartFile file) {
        try {
            String userIdStr = SecurityContextHolder.getContext().getAuthentication().getName();
            UUID ownerId = UUID.fromString(userIdStr);

            UserProfileResponse userProfile = authServiceClient.getUserById(ownerId);

            OwnerDetails ownerDetails = new OwnerDetails(
                    ownerId,
                    userProfile.getFullName(),
                    userProfile.email(),
                    userProfile.phone()
            );

            Result<FsboResponse, String> result = useCase.execute(file.getInputStream(), ownerDetails);

            if (!result.isSuccess()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result.getErrorValue());
            }

            return ResponseEntity.status(HttpStatus.ACCEPTED)
                    .body("Procesamiento en lote iniciado. Inmuebles válidos aceptados: " + result.getValue());

        } catch (feign.FeignException e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body("No se pudo verificar la identidad del usuario con el servicio de autenticación.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error interno procesando el archivo CSV.");
        }
    }
}