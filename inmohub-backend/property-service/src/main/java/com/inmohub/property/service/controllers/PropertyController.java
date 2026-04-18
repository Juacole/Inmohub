package com.inmohub.property.service.controllers;

import com.inmohub.property.service.dtos.PropertyCreateDto;
import com.inmohub.property.service.dtos.PropertyDto;
import com.inmohub.property.service.services.PropertyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Controlador REST para la gestión de inmuebles.
 * Expone los endpoints para crear, consultar y eliminar propiedades.
 */
@RestController
@RequestMapping("/api/v1/properties")
@AllArgsConstructor
@Tag(name = "Gestión de Propiedades", description = "Endpoints para el ciclo de vida de los inmuebles (CRUD)")
public class PropertyController {
    private final PropertyService service;

    @Operation(summary = "Publicar una nueva propiedad", description = "Crea un inmueble validando previamente que el propietario exista y esté activo en Auth-Service.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Propiedad creada exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = PropertyDto.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o propietario no encontrado", content = @Content),
            @ApiResponse(responseCode = "409", description = "Conflicto: El propietario no tiene estado ACTIVE", content = @Content)
    })
    @PostMapping("/create")
    public ResponseEntity<PropertyDto> create(@Valid @RequestBody PropertyCreateDto createDTO) {
        return ResponseEntity.ok(service.createProperty(createDTO));
    }

    @Operation(summary = "Listar todas las propiedades", description = "Recupera el listado completo de inmuebles disponibles en el sistema.")
    @ApiResponse(responseCode = "200", description = "Listado recuperado correctamente")
    @GetMapping("/all")
    public ResponseEntity<List<PropertyDto>> getAll() {
        return ResponseEntity.ok(service.getAllProperties());
    }

    @Operation(summary = "Buscar propiedad por ID", description = "Obtiene los detalles de un inmueble específico.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Propiedad encontrada",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = PropertyDto.class))),
            @ApiResponse(responseCode = "404", description = "Propiedad no encontrada", content = @Content)
    })
    @GetMapping("/search-by-id/{id}")
    public ResponseEntity<PropertyDto> getById(@PathVariable(name = "id") UUID id) {
        PropertyDto p = service.getPropertyById(id);

        if(p != null) return ResponseEntity.ok(p);

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .build();
    }

    @Operation(summary = "Listar propiedades de un propietario", description = "Devuelve todos los inmuebles asociados a un usuario específico.")
    @ApiResponse(responseCode = "200", description = "Listado recuperado (puede estar vacío)")
    @GetMapping("/search-by-owner-id/{id}")
    public ResponseEntity<List<PropertyDto>> getByOwnerId(@PathVariable(name = "id") UUID id) {
        return ResponseEntity.ok(service.findByOwnerId(id));
    }

    @Operation(summary = "Eliminar propiedad", description = "Elimina físicamente un inmueble de la base de datos.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Propiedad eliminada correctamente"),
            @ApiResponse(responseCode = "404", description = "No se encontró la propiedad a eliminar")
    })
    @DeleteMapping("/delete-by-id/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable UUID id) {
        boolean deleted = service.deleteById(id);

        if (deleted) {
            return ResponseEntity
                    .ok()
                    .build();
        } else {
            return ResponseEntity
                    .notFound()
                    .build();
        }
    }
}
