package com.inmohub.auth.service.controllers;

import com.inmohub.auth.service.dtos.*;
import com.inmohub.auth.service.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Controlador REST para la gestión de usuarios.
 * Provee endpoints para registro, login y consulta de perfiles.
 */
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor // Lombok: Mejor práctica que AllArgsConstructor para inyección de dependencias final
@Tag(name = "Gestión de Usuarios", description = "Endpoints para autenticación y administración de perfiles")
public class AuthController {
    private final UserService userService;

    @PostMapping("/register")
    @Operation(
            summary = "Registrar nuevo usuario",
            description = "Crea un usuario con rol específico (ADMIN, AGENT, CLIENT, OWNER).",
            security = @SecurityRequirement(name = "Bearer")
    )
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "201", description = "Usuario creado exitosamente",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = UserDto.class),
                                examples = {
                                        @ExampleObject(
                                                name = "Usuario creado",
                                                value = "{\"id\":\"550e8400-e29b-41d4-a716-446655440000\",\"username\":\"pepemontana\",\"email\":\"pepe.montana@gmail.com\",\"firstName\":\"Pepe\",\"lastName\":\"Montana Botella\",\"phone\":\"600123456\",\"roles\":[\"AGENT\"],\"status\":\"ACTIVE\",\"createdAt\":\"2024-01-15T10:30:00\",\"updatedAt\":\"2024-01-15T10:30:00\"}"
                                        )
                                }
                        )
                ),
                @ApiResponse(
                        responseCode = "400",
                        description = "Datos de entrada inválidos - Validación fallida",
                        content = @Content(
                                mediaType = "application/json",
                                examples = {
                                        @ExampleObject(
                                                name = "Error de validación",
                                                value = "{\"error\":\"El username es obligatorio\",\"field\":\"username\"}"
                                        )
                                }
                        )
                ),
                @ApiResponse(
                        responseCode = "409",
                        description = "Conflicto: El email o username ya existen",
                        content = @Content(
                                mediaType = "application/json",
                                examples = {
                                        @ExampleObject(
                                                name = "Email duplicado",
                                                value = "{\"error\":\"El email pepe.montana@gmail.com ya está en uso\"}"
                                        )
                                }
                        )
                )
            }
    )
    public ResponseEntity<UserDto> create(
            @Valid
            @RequestBody(
                    description = "Datos del usuario a crear",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = UserCreateDto.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Ejemplo registro",
                                            value = "{\"username\":\"pepemontana\",\"password\":\"Segura123\",\"email\":\"pepe.montana@gmail.com\",\"firstName\":\"Pepe\",\"lastName\":\"Montana Botella\",\"phone\":\"600123456\",\"roles\":[\"AGENT\"]}"
                                    )
                            }
                    )
            )
            @org.springframework.web.bind.annotation.RequestBody
            UserCreateDto createDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(createDto));
    }

    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT')")
    @Operation(
            summary = "Listar todos los usuarios",
            description = "Devuelve un listado completo de los usuarios registrados en el sistema. Solo accesible para ADMIN y AGENT.",
            security = @SecurityRequirement(name = "Bearer")
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Operación exitosa - Lista de usuarios",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = UserDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "No autorizado - Token JWT inválido o ausente"
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Prohibido - El usuario no tiene el rol requerido"
                    )
            }
    )
    public ResponseEntity<List<UserDto>> getAll() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/search-by-id/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT')")
    @Operation(
            summary = "Obtener usuario por ID",
            description = "Busca un usuario específico por su UUID. Solo accesible para ADMIN y AGENT.",
            security = @SecurityRequirement(name = "Bearer")
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Usuario encontrado",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = UserDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "ID inválido - Formato UUID incorrecto"
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "No autorizado - Token JWT inválido o ausente"
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Prohibido - El usuario no tiene el rol requerido"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Usuario no encontrado",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = {
                                            @ExampleObject(
                                                    name = "No encontrado",
                                                    value = "{\"error\":\"No se encontró usuario con id: 550e8400-e29b-41d4-a716-446655440000\"}"
                                            )
                                    }
                            )
                    )
            }
    )
    public ResponseEntity<UserDto> getById(
            @Parameter(
                    description = "UUID del usuario a buscar",
                    required = true,
                    example = "550e8400-e29b-41d4-a716-446655440000"
            )
            @PathVariable(name = "id") UUID id) {
        UserDto user = userService.getById(id);

        if(user != null) return ResponseEntity.ok(user);

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .build();
    }

    @GetMapping("/exists-by-email/{email}")
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT')")
    @Operation(
            summary = "Verificar existencia por Email",
            description = "Comprueba si un email ya está registrado en el sistema. Solo accesible para ADMIN y AGENT.",
            security = @SecurityRequirement(name = "Bearer")
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "El email existe - Retorna true"
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "No autorizado - Token JWT inválido o ausente"
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Prohibido - El usuario no tiene el rol requerido"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "El email no existe - Retorna 404 (false)"
                    )
            }
    )
    public ResponseEntity<Boolean> existsByEmail(
            @Parameter(
                    description = "Email a verificar",
                    required = true,
                    example = "pepe.montana@gmail.com"
            )
            @PathVariable(name = "email") String email) {
        boolean existe = userService.existsByEmail(email);
        if(existe) return ResponseEntity.ok(existe);

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .build();
    }

    @GetMapping("/exists-by-username/{username}")
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT')")
    @Operation(
            summary = "Verificar existencia por Username",
            description = "Comprueba si un nombre de usuario ya está en uso. Solo accesible para ADMIN y AGENT.",
            security = @SecurityRequirement(name = "Bearer")
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "El username existe - Retorna true"
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "No autorizado - Token JWT inválido o ausente"
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Prohibido - El usuario no tiene el rol requerido"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "El username no existe - Retorna 404 (false)"
                    )
            }
    )
    public ResponseEntity<Boolean> existsByUsername(
            @Parameter(
                    description = "Nombre de usuario a verificar",
                    required = true,
                    example = "pepemontana"
            )
            @PathVariable(name = "username") String username) {
        boolean existe = userService.existsByUsername(username);

        if(existe) return ResponseEntity.ok(existe);

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .build();
    }

    @DeleteMapping("/delete-by-id/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT', 'CLIENT', 'OWNER')")
    @Operation(
            summary = "Eliminar usuario",
            description = "Elimina físicamente un usuario de la base de datos por su ID. El usuario solo puede eliminarse a sí mismo o ser eliminado por un ADMIN.",
            security = @SecurityRequirement(name = "Bearer")
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Usuario eliminado correctamente",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = {
                                            @ExampleObject(
                                                    name = "Eliminado",
                                                    value = "{\"message\":\"Usuario eliminado exitosamente\"}"
                                            )
                                    }
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "ID inválido - Formato UUID incorrecto"
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "No autorizado - Token JWT inválido o ausente"
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Prohibido - El usuario no tiene permisos para eliminar este usuario"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "No se encontró el usuario para eliminar"
                    )
            }
    )
    public ResponseEntity<Void> deleteById(
            @Parameter(
                    description = "UUID del usuario a eliminar",
                    required = true,
                    example = "550e8400-e29b-41d4-a716-446655440000"
            )
            @PathVariable UUID id) {
        boolean eliminado = userService.deleteById(id);

        if (eliminado) {
            return ResponseEntity
                    .ok()
                    .build();
        } else {
            return ResponseEntity
                    .notFound()
                    .build();
        }
    }

    @PostMapping("/login")
    @Operation(
            summary = "Iniciar Sesión",
            description = "Verifica credenciales (email y password) y devuelve un token de acceso JWT junto con un refresh token."
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Login correcto - Tokens generados",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = AuthResponseDto.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "Login exitoso",
                                                    value = "{\"accessToken\":\"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...\",\"refreshToken\":\"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...\"}"
                                            )
                                    }
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Solicitud inválida - Datos de entrada incorrectos"
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Credenciales incorrectas - Email o contraseña inválidos",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = {
                                            @ExampleObject(
                                                    name = "Credenciales inválidas",
                                                    value = "{\"error\":\"Credenciales incorrectas\"}"
                                            )
                                    }
                            )
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Usuario deshabilitado - El usuario está inactivo"
                    )
            }
    )
    public ResponseEntity<AuthResponseDto> login(
            @RequestBody(
                    description = "Credenciales de acceso",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = LoginDto.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Ejemplo login",
                                            value = "{\"email\":\"pepe.montana@gmail.com\",\"password\":\"Password123\"}"
                                    )
                            }
                    )
            )
            @org.springframework.web.bind.annotation.RequestBody
            LoginDto loginDto) {
        return ResponseEntity.ok(userService.login(loginDto.email(), loginDto.password()));
    }

    @PostMapping("/refresh")
    @Operation(
            summary = "Refrescar Token de Acceso",
            description = "Genera un nuevo JWT de acceso usando un refresh token válido. El refresh token tiene una vida más larga que el token de acceso."
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Tokens refrescados exitosamente",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = AuthResponseDto.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "Tokens refrescados",
                                                    value = "{\"accessToken\":\"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...\",\"refreshToken\":\"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...\"}"
                                            )
                                    }
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Solicitud inválida - Refresh token faltante o formato incorrecto"
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Token expirado o inválido - El refresh token no es válido o ha expirado"
                    )
            }
    )
    public ResponseEntity<AuthResponseDto> refreshToken(
            @Valid
            @RequestBody(
                    description = "Refresh token para obtener nuevos accesos",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = RefreshTokenRequestDto.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Ejemplo refresh",
                                            value = "{\"refreshToken\":\"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...\"}"
                                    )
                            }
                    )
            )
            @org.springframework.web.bind.annotation.RequestBody
            RefreshTokenRequestDto requestDto) {
        return ResponseEntity.ok(userService.refreshToken(requestDto.refreshToken()));
    }

    @GetMapping("/role/{userRole}")
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT')")
    @Operation(
            summary = "Listar usuarios por Rol",
            description = "Filtra y devuelve usuarios que tengan un rol específico (ADMIN, AGENT, OWNER, CLIENT). Solo accesible para ADMIN y AGENT.",
            security = @SecurityRequirement(name = "Bearer")
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Listado de usuarios con el rol especificado (puede estar vacío)",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = UserDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Rol inválido - El rol proporcionado no es válido"
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "No autorizado - Token JWT inválido o ausente"
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Prohibido - El usuario no tiene el rol requerido"
                    )
            }
    )
    public ResponseEntity<List<UserDto>> getByRole(
            @Parameter(
                    description = "Rol de usuario a buscar",
                    required = true,
                    example = "AGENT",
                    schema = @Schema(allowableValues = {"ADMIN", "AGENT", "CLIENT", "OWNER"})
            )
            @PathVariable String userRole) {
        List<UserDto> users = userService.getByRole(userRole);

        return ResponseEntity.ok(users);
    }
}
