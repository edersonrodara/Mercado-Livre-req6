package com.mercadolibre.grupo1.projetointegrador.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.time.Instant;

/**
 * @author Nayara Coca
 * trata exceções do controller
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExceptionDTO {
    private Integer status;
    private String message;
    private Instant timestamp;
    private String path;

    //erro 400
    public static ExceptionDTO badRequest(String message, String path) {
        return new ExceptionDTO(HttpStatus.BAD_REQUEST.value(), message, Instant.now(), path);
    }

    public static ExceptionDTO forbidden(String message, String path){
        return new ExceptionDTO(HttpStatus.FORBIDDEN.value(), message, Instant.now(), path);
    }

    public static ExceptionDTO unauthorized(String message, String path){
        return new ExceptionDTO(HttpStatus.UNAUTHORIZED.value(), message, Instant.now(), path);
    }
}
