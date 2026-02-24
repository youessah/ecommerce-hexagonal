package com.ecommerce.infrastructure.adapter.web.rest;

import com.ecommerce.domain.exception.AuthenticationException;
import com.ecommerce.domain.exception.OrderNotFoundException;
import com.ecommerce.domain.exception.ProductNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

/**
 * ADAPTATEUR - Gestionnaire global des erreurs HTTP.
 * Traduit les exceptions métier en réponses HTTP appropriées.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuth(AuthenticationException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse(HttpStatus.UNAUTHORIZED.value(), e.getMessage(), LocalDateTime.now()));
    }

    @ExceptionHandler({ProductNotFoundException.class, OrderNotFoundException.class})
    public ResponseEntity<ErrorResponse> handleNotFound(RuntimeException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(HttpStatus.NOT_FOUND.value(), e.getMessage(), LocalDateTime.now()));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleBusinessRule(IllegalStateException e) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(new ErrorResponse(422, e.getMessage(), LocalDateTime.now()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(f -> f.getField() + ": " + f.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), message, LocalDateTime.now()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneral(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(500, "Erreur interne du serveur: " + e.getMessage(), LocalDateTime.now()));
    }

    public record ErrorResponse(int status, String message, LocalDateTime timestamp) {}
}
