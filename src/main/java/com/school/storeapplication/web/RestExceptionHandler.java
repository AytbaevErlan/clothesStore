package com.school.storeapplication.web;

import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestControllerAdvice
public class RestExceptionHandler {
    public record Err(String message, Map<String,String> details) {}

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Err> handleValidation(MethodArgumentNotValidException ex) {
        Map<String,String> m = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(e -> m.put(e.getField(), e.getDefaultMessage()));
        return ResponseEntity.badRequest().body(new Err("validation failed", m));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Err> handleConstraint(ConstraintViolationException ex) {
        Map<String,String> m = new LinkedHashMap<>();
        ex.getConstraintViolations().forEach(v -> m.put(v.getPropertyPath().toString(), v.getMessage()));
        return ResponseEntity.badRequest().body(new Err("validation failed", m));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Err> handleIntegrity(DataIntegrityViolationException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new Err("conflict", Map.of()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Err> handleIllegal(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(new Err(ex.getMessage(), Map.of()));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Err> handleIllegalState(IllegalStateException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new Err(ex.getMessage(), Map.of()));
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<Err> handleNotFound(NoSuchElementException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Err("not found", Map.of()));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Err> handleRuntime(RuntimeException ex) {
        ex.printStackTrace(); // temp for exam
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new Err(ex.getMessage() == null ? "server error" : ex.getMessage(), Map.of()));
    }
}
