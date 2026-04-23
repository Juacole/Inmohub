package com.inmohub.lead.service.infrastructure.adapters.in.web.advice;

import com.inmohub.lead.service.application.usecases.errors.LeadNotFound;
import com.inmohub.lead.service.domain.abstractions.Error;
import com.inmohub.lead.service.domain.abstractions.Result;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.util.Map;

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
        if (error instanceof LeadNotFound) {
            return HttpStatus.NOT_FOUND;
        }

        return HttpStatus.BAD_REQUEST;
    }
}