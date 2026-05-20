package com.inmohub.property.service.controllers;

import com.inmohub.property.service.dtos.PropertyCreateDto;
import com.inmohub.property.service.dtos.PropertyDto;
import com.inmohub.property.service.dtos.PropertyPatchDto;
import com.inmohub.property.service.dtos.PropertyPhotoDto;
import com.inmohub.property.service.dtos.PropertySearchCriteria;
import com.inmohub.property.service.dtos.PropertySummaryDto;
import com.inmohub.property.service.services.PropertyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import io.swagger.v3.oas.annotations.parameters.RequestBody;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Controlador REST para la gestión de inmuebles.
 * Expone los endpoints para crear, consultar y eliminar propiedades.
 */
@RestController
@RequestMapping("/api/v1/properties")
@AllArgsConstructor
@Tag(
        name = "Gestión de Propiedades",
        description = "Endpoints para el ciclo de vida completo de los inmuebles: creación, consulta, actualización y eliminación. " +
                "Requiere autenticación JWT para operaciones protegidas."
)
@SecurityRequirement(name = "bearerAuth")
public class PropertyController {

    private final PropertyService propertyService;

    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT', 'OWNER')")
    @Operation(
            summary = "Publicar una nueva propiedad con imágenes",
            description = "Crea un inmueble vinculando imágenes subidas a Firebase. " +
                    "El owner_id se extrae automáticamente del token de seguridad (claim 'sub'). " +
                    "Permite subir hasta 10 imágenes en formato JPG, JPEG o PNG (máximo 5MB por archivo). " +
                    "Las imágenes se almacenan en Firebase Storage y se asocian a la propiedad."
    )
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "201",
                        description = "Propiedad creada exitosamente",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = PropertyDto.class),
                                examples = @ExampleObject(
                                        name = "Propiedad creada",
                                        value = "{\"id\":\"550e8400-e29b-41d4-a716-446655440000\",\"title\":\"Chalet de lujo\",\"description\":\"Hermosa casa\",\"price\":450000.00,\"areaM2\":250.5,\"address\":\"Calle Mayor 123\",\"city\":\"Madrid\",\"status\":\"AVAILABLE\",\"ownerId\":\"660e8400-e29b-41d4-a716-446655440000\",\"photos\":[],\"features\":[],\"createdAt\":\"2024-01-15T10:30:00\",\"updatedAt\":\"2024-01-15T10:30:00\"}"
                                )
                        )
                ),
                @ApiResponse(
                        responseCode = "400",
                        description = "Error de validación en los datos de entrada. Verificar formato JSON y campos obligatorios.",
                        content = @Content(mediaType = "application/json",
                                examples = @ExampleObject(
                                        value = "{\"errors\":{\"title\":\"El título es obligatorio\",\"price\":\"El precio debe ser positivo\"}}"
                                ))
                ),
                @ApiResponse(
                        responseCode = "401",
                        description = "No autenticado. Token JWT no proporcionado o inválido.",
                        content = @Content
                ),
                @ApiResponse(
                        responseCode = "403",
                        description = "No autorizado. El usuario no tiene el rol requerido (ADMIN, AGENT u OWNER).",
                        content = @Content
                ),
                @ApiResponse(
                        responseCode = "409",
                        description = "Conflicto: El propietario no existe o no está activo en el sistema.",
                        content = @Content(mediaType = "application/json",
                                examples = @ExampleObject(
                                        value = "{\"error\":\"El propietario no existe o no está activo\"}"
                                ))
                ),
                @ApiResponse(
                        responseCode = "500",
                        description = "Error interno del servidor al procesar la solicitud.",
                        content = @Content
                )
            }
    )
    public ResponseEntity<PropertyDto> create(
            @Parameter(
                    description = "Metadatos de la propiedad en formato JSON. " +
                            "Ejemplo: {\"title\":\"Casa\",\"description\":\"...\",\"price\":250000,\"areaM2\":120,\"address\":\"Calle X\",\"city\":\"Madrid\",\"state\":\"Madrid\",\"country\":\"España\"}",
                    required = true,
                    schema = @Schema(example = "{\"title\":\"Chalet de lujo\",\"description\":\"Hermosa casa\",\"price\":450000.00,\"areaM2\":250.5,\"address\":\"Calle Mayor 123\",\"city\":\"Madrid\",\"state\":\"Comunidad de Madrid\",\"country\":\"España\",\"features\":[{\"featureName\":\"Habitaciones\",\"featureValue\":\"4\"}]}")
            )
            @RequestPart("property") @Valid PropertyCreateDto propertyCreateDto,
            @Parameter(description = "Archivos de imagen opcionales ( formatos permitidos: JPG, JPEG, PNG). Máximo 5MB por archivo.")
            @RequestPart(value = "photos", required = false) List<MultipartFile> photos
    ) throws IOException {

        // Recuperación del ID del usuario desde SecurityContext (inyectado por HeaderAuthenticationFilter)
        String userIdHeader = SecurityContextHolder.getContext().getAuthentication().getName();
        UUID ownerId = UUID.fromString(userIdHeader);

        PropertyDto response = propertyService.createProperty(propertyCreateDto, photos, ownerId);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/all")
    @Operation(
            summary = "Listar todas las propiedades",
            description = "Recupera el listado completo de inmuebles disponibles en el sistema. " +
                    "Retorna un array vacío si no existen propiedades. " +
                    "Este endpoint es público (no requiere autenticación)."
    )
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Listado recuperado correctamente",
                        content = @Content(
                                mediaType = "application/json",
                                array = @ArraySchema(schema = @Schema(implementation = PropertyDto.class))
                        )
                ),
                @ApiResponse(
                        responseCode = "500",
                        description = "Error interno del servidor al recuperar las propiedades.",
                        content = @Content
                )
            }
    )
    public ResponseEntity<List<PropertyDto>> getAll() {
        return ResponseEntity.ok(propertyService.getAllProperties());
    }

    @GetMapping("/search-by-id/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT', 'OWNER', 'CLIENT')")
    @Operation(
            summary = "Buscar propiedad por ID",
            description = "Obtiene los detalles completos de un inmueble específico mediante su identificador único (UUID). " +
                    "Solo accesible para usuarios con rol ADMIN o AGENT."
    )
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Propiedad encontrada correctamente",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = PropertyDto.class)
                        )
                ),
                @ApiResponse(
                        responseCode = "401",
                        description = "No autenticado. Token JWT no proporcionado o inválido.",
                        content = @Content
                ),
                @ApiResponse(
                        responseCode = "403",
                        description = "No autorizado. Se requiere rol ADMIN o AGENT.",
                        content = @Content
                ),
                @ApiResponse(
                        responseCode = "404",
                        description = "No se encontró ninguna propiedad con el ID especificado.",
                        content = @Content(mediaType = "application/json",
                                examples = @ExampleObject(
                                        value = "{\"error\":\"Propiedad no encontrada con ID: 550e8400-e29b-41d4-a716-446655440000\"}"
                                ))
                ),
                @ApiResponse(
                        responseCode = "500",
                        description = "Error interno del servidor.",
                        content = @Content
                )
            }
    )
    public ResponseEntity<PropertyDto> getById(
            @Parameter(
                    description = "Identificador único (UUID) de la propiedad",
                    example = "550e8400-e29b-41d4-a716-446655440000"
            )
            @PathVariable(name = "id") UUID id) {
        PropertyDto p = propertyService.getPropertyById(id);

        if(p != null) return ResponseEntity.ok(p);

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .build();
    }

    @GetMapping("/search-by-owner-id/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT', 'OWNER')")
    @Operation(
            summary = "Listar propiedades de un propietario",
            description = "Devuelve todos los inmuebles asociados a un usuario específico (propietario). " +
                    "Retorna un array vacío si el usuario no tiene propiedades registradas. " +
                    "Los OWNER solo pueden ver sus propias propiedades."
    )
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Listado de propiedades del propietario recuperado correctamente",
                        content = @Content(
                                mediaType = "application/json",
                                array = @ArraySchema(schema = @Schema(implementation = PropertyDto.class))
                        )
                ),
                @ApiResponse(
                        responseCode = "401",
                        description = "No autenticado. Token JWT no proporcionado o inválido.",
                        content = @Content
                ),
                @ApiResponse(
                        responseCode = "403",
                        description = "No autorizado. Se requiere rol ADMIN, AGENT u OWNER.",
                        content = @Content
                ),
                @ApiResponse(
                        responseCode = "500",
                        description = "Error interno del servidor.",
                        content = @Content
                )
            }
    )
    public ResponseEntity<List<PropertyDto>> getByOwnerId(
            @Parameter(description = "Identificador único (UUID) del propietario", example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable(name = "id") UUID id) {
        return ResponseEntity.ok(propertyService.findByOwnerId(id));
    }

    @GetMapping("/summary")
    @Operation(
            summary = "Catálogo optimizado de propiedades",
            description = "Devuelve una lista paginada de inmuebles con información mínima y solo la foto principal para optimizar la carga del frontend."
    )
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Catálogo de propiedades recuperado correctamente",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = PropertySummaryDto.class)
                        )
                ),
                @ApiResponse(
                        responseCode = "500",
                        description = "Error interno del servidor al recuperar el catálogo de propiedades.",
                        content = @Content
                )
            }
    )
    public ResponseEntity<Page<PropertySummaryDto>> getPropertiesSummary(
            @Parameter(
                    description = "Parámetros de paginación: page (número de página), size (tamaño de página), sort (ordenamiento)"
            )
            Pageable pageable
    ) {
        return ResponseEntity.ok(propertyService.getPropertiesSummary(pageable));
    }

    @GetMapping("/search")
    @Operation(
            summary = "Búsqueda dinámica de propiedades con filtros",
            description = "Permite filtrar propiedades por ciudad, rango de precios y estado. " +
                    "Todos los filtros son opcionales y se combinan con AND. La paginación es obligatoria."
    )
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Resultados de la búsqueda filtrada",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = PropertySummaryDto.class)
                        )
                ),
                @ApiResponse(
                        responseCode = "500",
                        description = "Error interno del servidor.",
                        content = @Content
                )
            }
    )
    public ResponseEntity<Page<PropertySummaryDto>> searchProperties(
            @Parameter(description = "Ciudad para filtrar", example = "Madrid")
            @RequestParam(required = false) String city,

            @Parameter(description = "Precio mínimo", example = "100000.00")
            @RequestParam(required = false) BigDecimal minPrice,

            @Parameter(description = "Precio máximo", example = "500000.00")
            @RequestParam(required = false) BigDecimal maxPrice,

            @Parameter(description = "Estado de la propiedad: AVAILABLE, SOLD, RENTED, OFF_MARKET", example = "AVAILABLE")
            @RequestParam(required = false) String status,

            @Parameter(description = "Parámetros de paginación: page (0-based), size, sort")
            Pageable pageable
    ) {
        PropertySearchCriteria criteria = new PropertySearchCriteria(city, minPrice, maxPrice, status);
        return ResponseEntity.ok(propertyService.searchProperties(criteria, pageable));
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT') or @propertyService.isOwner(#id, authentication.name)")
    @Operation(
            summary = "Actualizar propiedad parcialmente",
            description = "Actualiza los datos de un inmueble existente. Solo los campos proporcionados en el body serán modificados. " +
                    "Solo el propietario original, ADMIN o AGENT pueden modificar. " +
                    "Si se incluye el array 'features', las características se reemplazan por completo.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Propiedad actualizada correctamente",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = PropertyDto.class),
                                    examples = @ExampleObject(
                                            name = "Propiedad actualizada",
                                            value = "{\"id\":\"550e8400-e29b-41d4-a716-446655440000\",\"title\":\"Chalet renovado\",\"description\":\"Propiedad completamente reformada\",\"price\":480000.00,\"areaM2\":250.5,\"address\":\"Calle Mayor 123\",\"city\":\"Madrid\",\"state\":\"Comunidad de Madrid\",\"country\":\"España\",\"status\":\"AVAILABLE\",\"ownerId\":\"660e8400-e29b-41d4-a716-446655440000\",\"photos\":[],\"features\":[{\"featureName\":\"Habitaciones\",\"featureValue\":\"5\"}],\"createdAt\":\"2024-01-15T10:30:00\",\"updatedAt\":\"2024-03-20T14:45:00\"}"
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Datos de entrada inválidos",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(
                                            value = "{\"errors\":{\"price\":\"El precio debe ser positivo\"}}"
                                    ))
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "No autenticado. Token JWT no proporcionado o inválido.",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "No autorizado. Solo el propietario original, ADMIN o AGENT pueden modificar.",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Propiedad no encontrada con el ID especificado.",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(
                                            value = "{\"error\":\"Propiedad no encontrada con ID: 550e8400-e29b-41d4-a716-446655440000\"}"
                                    ))
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Error interno del servidor.",
                            content = @Content
                    )
            }
    )
    public ResponseEntity<PropertyDto> updateProperty(
            @Parameter(description = "Identificador único (UUID) de la propiedad a actualizar", example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable UUID id,
            @RequestBody(
                    description = "Campos a actualizar de la propiedad (todos opcionales). Solo los campos enviados serán modificados.",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = PropertyPatchDto.class),
                            examples = @ExampleObject(
                                    value = "{\"title\":\"Chalet renovado\",\"description\":\"Nueva descripción\",\"price\":480000.00,\"status\":\"RESERVED\",\"features\":[{\"featureName\":\"Habitaciones\",\"featureValue\":\"5\"}]}"
                            )
                    )
            )
            PropertyPatchDto dto) {
        return ResponseEntity.ok(propertyService.patchProperty(id, dto));
    }

    @PostMapping(value = "/{id}/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT') or @propertyService.isOwner(#id, authentication.name)")
    @Operation(
            summary = "Añadir imágenes a una propiedad",
            description = "Sube y anexa nuevas imágenes a un inmueble en Firebase Storage. " +
                    "Si la propiedad no tiene fotos previas, la primera imagen enviada se marca como foto principal. " +
                    "Solo el propietario original, ADMIN o AGENT pueden añadir imágenes. " +
                    "Formatos permitidos: JPG, JPEG, PNG (máximo 5MB por archivo).",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Imágenes añadidas correctamente",
                            content = @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = PropertyPhotoDto.class)),
                                    examples = @ExampleObject(
                                            name = "Fotos actualizadas",
                                            value = "[{\"id\":\"770e8400-e29b-41d4-a716-446655440000\",\"photoUrl\":\"https://firebasestorage.googleapis.com/.../img1.jpg\",\"isPrimary\":true},{\"id\":\"880e8400-e29b-41d4-a716-446655440000\",\"photoUrl\":\"https://firebasestorage.googleapis.com/.../img2.jpg\",\"isPrimary\":false}]"
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "No autenticado. Token JWT no proporcionado o inválido.",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "No autorizado. Solo el propietario original, ADMIN o AGENT pueden modificar.",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Propiedad no encontrada con el ID especificado.",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(
                                            value = "{\"error\":\"Propiedad no encontrada con ID: 550e8400-e29b-41d4-a716-446655440000\"}"
                                    ))
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Error interno del servidor al procesar las imágenes.",
                            content = @Content
                    )
            }
    )
    public ResponseEntity<List<PropertyPhotoDto>> addImages(
            @Parameter(description = "Identificador único (UUID) de la propiedad", example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable UUID id,
            @Parameter(description = "Archivos de imagen a subir (formatos permitidos: JPG, JPEG, PNG). Máximo 5MB por archivo.")
            @RequestPart("photos") List<MultipartFile> photos
    ) throws IOException {
        return ResponseEntity.ok(propertyService.addImages(id, photos));
    }

    @DeleteMapping("/delete-by-id/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT', 'OWNER')")
    @Operation(
            summary = "Eliminar propiedad",
            description = "Elimina físicamente un inmueble de la base de datos. " +
                    "NOTA: Solo el propietario con el mismo ownerId, ADMIN o AGENT pueden eliminar propiedades. " +
                    "Esta operación es irreversible."
    )
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Propiedad eliminada correctamente",
                        content = @Content(mediaType = "application/json",
                                examples = @ExampleObject(
                                        value = "{\"message\":\"Propiedad eliminada exitosamente\"}"
                                ))
                ),
                @ApiResponse(
                        responseCode = "401",
                        description = "No autenticado. Token JWT no proporcionado o inválido.",
                        content = @Content
                ),
                @ApiResponse(
                        responseCode = "403",
                        description = "No autorizado. El usuario no tiene permisos para eliminar esta propiedad.",
                        content = @Content
                ),
                @ApiResponse(
                        responseCode = "404",
                        description = "No se encontró ninguna propiedad con el ID especificado para eliminar.",
                        content = @Content(mediaType = "application/json",
                                examples = @ExampleObject(
                                        value = "{\"error\":\"Propiedad no encontrada con ID: 550e8400-e29b-41d4-a716-446655440000\"}"
                                ))
                ),
                @ApiResponse(
                        responseCode = "500",
                        description = "Error interno del servidor.",
                        content = @Content
                )
            }
    )
    public ResponseEntity<Void> deleteById(
            @Parameter(description = "Identificador único (UUID) de la propiedad a eliminar", example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable UUID id) {
        boolean deleted = propertyService.deleteById(id);

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
