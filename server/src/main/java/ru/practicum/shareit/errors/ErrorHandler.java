package ru.practicum.shareit.errors;

import jakarta.persistence.EntityNotFoundException;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.nio.file.AccessDeniedException;
import java.util.Map;
import java.util.NoSuchElementException;

@RestControllerAdvice
public class ErrorHandler {

    private static final Logger log = LoggerFactory.getLogger(ErrorHandler.class);

    @ExceptionHandler(TransactionSystemException.class)
    public ResponseEntity<Object> handleTransaction(TransactionSystemException ex) {
        log.error("Transaction error", ex);
        if (ex.getCause() instanceof ConstraintViolationException) {
            return ResponseEntity.badRequest().body(Map.of("error", "validation failed"));
        }
        return ResponseEntity.internalServerError().body(Map.of("error", "internal error"));
    }

    @ExceptionHandler({
            NoSuchElementException.class,
            EntityNotFoundException.class,
            EmptyResultDataAccessException.class
    })
    public ResponseEntity<Object> handleNotFound(RuntimeException ex) {
        log.warn("Not found: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", "resource not found"));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Object> handleConflict(DataIntegrityViolationException ex) {
        log.error("Data integrity violation", ex);
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("error", "data integrity violation"));
    }

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<Object> handleAny(Throwable ex) {
        log.error("Unexpected error", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "internal error"));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Object> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String msg = "bad request: parameter '%s' has invalid value '%s'"
                .formatted(ex.getName(), ex.getValue());
        log.warn("Type mismatch: {}", msg, ex);
        return ResponseEntity.badRequest().body(Map.of("error", msg));
    }

    @ExceptionHandler({AccessDeniedException.class, SecurityException.class})
    public ResponseEntity<Object> handleForbidden(Exception ex) {
        log.warn("Forbidden: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("error", ex.getMessage() == null ? "forbidden" : ex.getMessage()));
    }
}
