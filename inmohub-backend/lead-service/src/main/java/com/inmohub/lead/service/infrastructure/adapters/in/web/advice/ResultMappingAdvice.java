package com.inmohub.lead.service.infrastructure.adapters.in.web.advice;

import com.inmohub.lead.service.application.usecases.errors.AgentNotFoundError;
import com.inmohub.lead.service.application.usecases.errors.ForbiddenError;
import com.inmohub.lead.service.application.usecases.errors.InvalidStatusError;
import com.inmohub.lead.service.application.usecases.errors.LeadAlreadyExistsError;
import com.inmohub.lead.service.application.usecases.errors.LeadNotFound;
import com.inmohub.lead.service.application.usecases.errors.PaginationError;
import com.inmohub.lead.service.application.usecases.errors.PropertyNotFoundError;
import com.inmohub.lead.service.application.usecases.errors.ValidationError;
import com.inmohub.lead.service.domain.abstractions.Error;
import com.inmohub.lead.service.domain.abstractions.Result;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.util.Map;

/**
 * Advice de Spring que intercepta las respuestas del controlador.
 * Desempaqueta objetos {@link Result}: en caso de exito retorna el valor,
 * en caso de error mapea el error a su codigo HTTP correspondiente.
 */
@RestControllerAdvice
public class ResultMappingAdvice implements ResponseBodyAdvice<Object> {

    // Solo se intercepta el método del controlador si este devuelve un Result
    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return returnType.getParameterType().isAssignableFrom(Result.class);
    }

    @Override
    public Object beforeBodyWrite(
            Object body,
            MethodParameter returnType,
            MediaType selectedContentType,
            Class<? extends HttpMessageConverter<?>> selectedConverterType,
            ServerHttpRequest request,
            ServerHttpResponse response
    ) {
        if (body instanceof Result<?, ?> result) {
            if (result.isSuccess()) { // Si la respuesta es exitosa se devuelve el valor de Result
                Object value = result.getValue();
                if (value == null) {
                    response.setStatusCode(HttpStatus.NO_CONTENT);
                    return null;
                }
                return value;
            } else { // En ccaso contrario se mapea al status code correspondiente
                Error error = (Error) result.getErrorValue().orElse(null);
                HttpStatus status = mapErrorToStatus(error);

                response.setStatusCode(status);

                return Map.of(
                        "status", status.value(),
                        "error", status.getReasonPhrase(),
                        "message", error != null ? error.getMessage() : "Error desconocido"
                );
            }
        }
        return body;
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, Object> handelAllException(Exception ex) {
        return Map.of(
                "status", 500,
                "error", "Internal Server Error",
                "message", "Ocurrio un error inesperadoo en el servidor."
        );
    }

    /**
     * Método auxiliar para recopilar todos los errores
     * personalizados y traducirlo a su equivalente en
     * un HttpStatus.
     */
    private HttpStatus mapErrorToStatus(Error error) {
        if (error instanceof LeadNotFound) return HttpStatus.NOT_FOUND;
        if (error instanceof LeadAlreadyExistsError) return HttpStatus.CONFLICT;
        if (error instanceof ForbiddenError) return HttpStatus.FORBIDDEN;
        if (error instanceof InvalidStatusError) return HttpStatus.BAD_REQUEST;
        if (error instanceof AgentNotFoundError) return HttpStatus.NOT_FOUND;
        if (error instanceof PropertyNotFoundError) return HttpStatus.NOT_FOUND;
        if (error instanceof PaginationError) return HttpStatus.BAD_REQUEST;
        if (error instanceof ValidationError) return HttpStatus.BAD_REQUEST;

        return HttpStatus.BAD_REQUEST;
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Map<String, Object> handleAccessDeniedException(AccessDeniedException ex) {
        return Map.of(
                "status", 403,
                "error", "Forbidden",
                "message", "Acceso denegado. No tienes permisos para visualizar estos leads."
        );
    }
}