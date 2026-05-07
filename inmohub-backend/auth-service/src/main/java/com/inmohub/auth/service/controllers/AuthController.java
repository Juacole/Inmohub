package com.inmohub.auth.service.controllers;

import com.inmohub.auth.service.dtos.*;
import com.inmohub.auth.service.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
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
            description = "Crea un usuario con rol específico (ADMIN, AGENT, CLIENT, OWNER)."
    )
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200", description = "Usuario creado exitosamente",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = UserDto.class)
                        )
                ),
                @ApiResponse(
                        responseCode = "400",
                        description = "Datos de entrada inválidos",
                        content = @Content
                ),
                @ApiResponse(
                        responseCode = "409",
                        description = "Conflicto: El email o username ya existen",
                        content = @Content
                )
            }
    )
    public ResponseEntity<UserDto> create(@Valid @RequestBody UserCreateDto createDto) {
        return ResponseEntity.ok(userService.createUser(createDto));
    }

    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT')")
    @Operation(
            summary = "Listar todos los usuarios",
            description = "Devuelve un listado completo de los usuarios registrados."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Operación exitosa"
    )
    public ResponseEntity<List<UserDto>> getAll() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/search-by-id/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT')")
    @Operation(
            summary = "Obtener usuario por ID",
            description = "Busca un usuario específico por su UUID."
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
                        responseCode = "404",
                        description = "Usuario no encontrado",
                        content = @Content
                )
            }
    )
    public ResponseEntity<UserDto> getById(@PathVariable(name = "id") UUID id) {
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
            description = "Comprueba si un email ya está registrado en el sistema."
    )
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "El email existe"
                ),
                @ApiResponse(
                        responseCode = "404",
                        description = "El email no existe"
                )
            }
    )
    public ResponseEntity<Boolean> existsByEmail(@PathVariable(name = "email") String email) {
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
            description = "Comprueba si un nombre de usuario ya está en uso."
    )
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "El username existe"
                ),
                @ApiResponse(
                        responseCode = "404",
                        description = "El username no existe"
                )
            }
    )
    public ResponseEntity<Boolean> existsByUsername(@PathVariable(name = "username") String username) {
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
            description = "Elimina físicamente un usuario de la base de datos por su ID."
    )
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Usuario eliminado correctamente"
                ),
                @ApiResponse(
                        responseCode = "404",
                        description = "No se encontró el usuario para eliminar"
                )
            }
    )
    public ResponseEntity<Void> deleteById(@PathVariable UUID id) {
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
            description = "Verifica credenciales (email y password) y devuelve el token generado."
    )
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Login correcto",
                        content = @Content(
                                schema = @Schema(implementation = Map.class)
                        )
                ),
                @ApiResponse(
                        responseCode = "500",
                        description = "Credenciales incorrectas o error interno",
                        content = @Content
                )
            }
    )
    public ResponseEntity<AuthResponseDto> login(@RequestBody LoginDto loginDto) {
        return ResponseEntity.ok(userService.login(loginDto.email(), loginDto.password()));
    }

    @PostMapping("/refresh")
    @Operation(
            summary = "Refrescar Token de Acceso",
            description = "Genera un nuevo JWT usando un Refresh Token válido."
    )
    public ResponseEntity<AuthResponseDto> refreshToken(@Valid @RequestBody RefreshTokenRequestDto requestDto) {
        return ResponseEntity.ok(userService.refreshToken(requestDto.refreshToken()));
    }

    @GetMapping("/role/{userRole}")
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT')")
    @Operation(
            summary = "Listar usuarios por Rol",
            description = "Filtra y devuelve usuarios que tengan un rol específico (ADMIN, AGENT, OWNER, CLIENT)."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Listado obtenido (puede estar vacío)"
    )
    public ResponseEntity<List<UserDto>> getByRole(@PathVariable String userRole) {
        List<UserDto> users = userService.getByRole(userRole);

        return ResponseEntity.ok(users);
    }
}
