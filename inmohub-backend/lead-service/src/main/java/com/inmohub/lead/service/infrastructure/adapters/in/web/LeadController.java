package com.inmohub.lead.service.infrastructure.adapters.in.web;

import com.inmohub.lead.service.application.dto.AssignLeadRequest;
import com.inmohub.lead.service.application.dto.ChangeStatusRequest;
import com.inmohub.lead.service.application.dto.CreateLeadRequest;
import com.inmohub.lead.service.application.dto.LeadAssignmentResponse;
import com.inmohub.lead.service.application.dto.LeadResponse;
import com.inmohub.lead.service.application.usecases.*;
import com.inmohub.lead.service.application.usecases.errors.AccessDeniedError;
import com.inmohub.lead.service.application.usecases.errors.ForbiddenAgentLeadsError;
import com.inmohub.lead.service.domain.abstractions.Error;
import com.inmohub.lead.service.domain.abstractions.Result;
import com.inmohub.lead.service.domain.abstractions.PaginatedResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/leads")
@RequiredArgsConstructor
@Tag(name = "Leads", description = "Gestión de clientes potenciales (Leads)")
public class LeadController {

    private final CreateLeadUseCase createLeadUseCase;
    private final AssignLeadUseCase assignLeadUseCase;
    private final GetAllLeadsUseCase getAllLeadsUseCase;
    private final GetLeadsByPropertyIdUseCase getLeadsByPropertyIdUseCase;
    private final GetLeadsByAgentIdUseCase getLeadsByAgentIdUseCase;
    private final ChangeLeadStatusUseCase changeLeadStatusUseCase;

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Crear un nuevo Lead",
            description = "Registra un interesado en una propiedad"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Lead creado exitosamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = LeadResponse.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos inválidos o regla de negocio no cumplida",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    example = "{\"status\": 400, \"error\": \"Bad Request\", \"message\": \"El email no puede estar vacío\"}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    example = "{\"status\": 500, \"error\": \"Internal Server Error\", \"message\": \"Ocurrió un error inesperado en el servidor.\"}"
                            )
                    )
            )
    })
    public Result<LeadResponse, Error> createLead(@RequestBody CreateLeadRequest request) {
        return createLeadUseCase.execute(request);
    }

    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Obtener todos los leads paginados",
            description = "Devuelve una lista paginada de todos los leads existentes"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Listado obtenido exitosamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = PaginatedResult.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    example = "{\"status\": 500, \"error\": \"Internal Server Error\", \"message\": \"Ocurrió un error inesperado en el servidor.\"}"
                            )
                    )
            )
    })
    public Result<PaginatedResult<LeadResponse>, Error> getAllLeads(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return getAllLeadsUseCase.execute(page, size);
    }

    @PostMapping("/{leadId}/assign")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Asignar un Lead a un Agente",
            description = "Cambia el estado del Lead a CONTACTED, registra la asignación y genera un log de auditoría."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Asignación exitosa",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = LeadAssignmentResponse.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos inválidos o el Lead no puede ser contactado (ej. ya está cerrado)",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    example = "{\"status\": 400, \"error\": \"Bad Request\", \"message\": \"No se puede contactar un lead cerrado o perdido.\"}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Lead no encontrado",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    example = "{\"status\": 404, \"error\": \"Not Found\", \"message\": \"Lead no encontrado\"}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    example = "{\"status\": 500, \"error\": \"Internal Server Error\", \"message\": \"Ocurrió un error inesperado en el servidor.\"}"
                            )
                    )
            )
    })
    public Result<LeadAssignmentResponse, Error> assignLead(
            @PathVariable UUID leadId,
            @RequestBody AssignLeadRequest request
    ) {
        String currentUserIdStr = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UUID actionUserId = currentUserIdStr != null ? UUID.fromString(currentUserIdStr) : null;

        return assignLeadUseCase.execute(leadId, request, actionUserId);
    }

    @GetMapping("/property/{propertyId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Obtener leads por ID de propiedad",
            description = "Devuelve una lista paginada de todos los interesados en una propiedad específica."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Listado de leads para la propiedad obtenido exitosamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = PaginatedResult.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Parámetros de paginación inválidos",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    example = "{\"status\": 400, \"error\": \"Bad Request\", \"message\": \"La página no puede ser negativa.\"}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    example = "{\"status\": 500, \"error\": \"Internal Server Error\", \"message\": \"Ocurrió un error inesperado en el servidor.\"}"
                            )
                    )
            )
    })
    public Result<PaginatedResult<LeadResponse>, Error> getLeadsByPropertyId(
            @PathVariable UUID propertyId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return getLeadsByPropertyIdUseCase.execute(propertyId, page, size);
    }

    @GetMapping("/agent/{agentId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Obtener leads por ID de agente",
            description = "Devuelve una lista paginada de los leads asignados a un agente específico."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Listado de leads del agente obtenido exitosamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = PaginatedResult.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Acceso denegado - El agente solo puede ver sus propios leads",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    example = "{\"status\": 403, \"error\": \"Forbidden\", \"message\": \"No puedes ver los leads de otro agente\"}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    example = "{\"status\": 500, \"error\": \"Internal Server Error\", \"message\": \"Ocurrió un error inesperado en el servidor.\"}"
                            )
                    )
            )
    })
    public Result<PaginatedResult<LeadResponse>, Error> getLeadsByAgentId(
            @PathVariable UUID agentId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        String currentUserIdStr = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String currentUserRole = SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse(null);

        if (!"AGENT".equals(currentUserRole) && !"ADMIN".equals(currentUserRole)) {
            return Result.error(new AccessDeniedError("Acceso denegado. Permisos insuficientes."));
        }

        if ("AGENT".equals(currentUserRole) && currentUserIdStr != null && !agentId.toString().equals(currentUserIdStr)) {
            return Result.error(new ForbiddenAgentLeadsError("No puedes ver los leads de otro agente."));
        }

        return getLeadsByAgentIdUseCase.execute(agentId, page, size);
    }

    @PatchMapping("/{leadId}/status")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Cambiar el estado de un lead",
            description = "Actualiza el estado de un lead. Requiere ser ADMIN o el AGENT asignado."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Estado actualizado exitosamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = LeadResponse.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Acceso denegado - Solo el ADMIN o el AGENT asignado puede modificar el lead",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    example = "{\"status\": 403, \"error\": \"Forbidden\", \"message\": \"No tienes permisos para modificar este lead.\"}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Lead no encontrado",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    example = "{\"status\": 404, \"error\": \"Not Found\", \"message\": \"Lead no encontrado.\"}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Estado inválido",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    example = "{\"status\": 400, \"error\": \"Bad Request\", \"message\": \"Estado inválido. Valores permitidos: NEW, CONTACTED, NEGOTIATION, CLOSED, LOST\"}"
                            )
                    )
            )
    })
    public Result<LeadResponse, Error> changeLeadStatus(
            @PathVariable UUID leadId,
            @RequestBody ChangeStatusRequest request,
            @RequestHeader("X-User-Id") UUID userId,
            @RequestHeader("X-User-Role") String userRole
    ) {
        return changeLeadStatusUseCase.execute(leadId, request.status(), userId, userRole);
    }
}