package ru.practicum.shareit.errors;

import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Map;

@RestControllerAdvice
public class ErrorHandler {

    private static final Logger log = LoggerFactory.getLogger(ErrorHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidation(MethodArgumentNotValidException ex) {
        String msg = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .findFirst().orElse("Validation failed");
        log.warn("Validation error: {}", msg, ex);
        return ResponseEntity.badRequest().body(Map.of("error", msg));
    }

    @ExceptionHandler({
            ConstraintViolationException.class,
            IllegalArgumentException.class,
            MethodArgumentTypeMismatchException.class,
            MissingServletRequestParameterException.class,
            HttpMessageNotReadableException.class
    })
    public ResponseEntity<Object> handleBadRequest(Exception ex) {
        log.warn("Bad request: {}", ex.getMessage(), ex);
        return ResponseEntity.badRequest().body(Map.of("error", "bad request"));
    }

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<Object> handleAny(Throwable ex) {
        log.error("Unexpected error: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "internal error"));
    }

    @ExceptionHandler(org.springframework.web.client.ResourceAccessException.class)
    public ResponseEntity<Object> serverUnavailable(org.springframework.web.client.ResourceAccessException ex) {
        log.error("Server unavailable: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of("error", "server unavailable"));
    }

}
