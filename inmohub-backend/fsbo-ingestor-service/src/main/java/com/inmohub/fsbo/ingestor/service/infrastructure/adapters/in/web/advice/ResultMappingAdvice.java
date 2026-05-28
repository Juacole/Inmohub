package com.inmohub.fsbo.ingestor.service.infrastructure.adapters.in.web.advice;

import com.inmohub.fsbo.ingestor.service.domain.abstractions.Result;
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

@RestControllerAdvice
/**
 * Advice que intercepta respuestas de controladores para transformar objetos {@link Result}
 * en respuestas HTTP estandar: 200 para exito, codigo de error en caso de fallo.
 */
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
                return result.getValue();
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

    private HttpStatus mapErrorToStatus(Error error) {
        return HttpStatus.INTERNAL_SERVER_ERROR;
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