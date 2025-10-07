package com.school.storeapplication.web;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class ApiErrors {
    @ExceptionHandler(org.springframework.dao.DataIntegrityViolationException.class)
    ResponseEntity<?> dup(org.springframework.dao.DataIntegrityViolationException ex){
        return ResponseEntity.status(409).body(Map.of("error","email or sku already exists"));
    }
}