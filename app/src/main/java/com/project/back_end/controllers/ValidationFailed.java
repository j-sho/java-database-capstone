package com.project.back_end.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * ValidationFailed - Global exception handler for REST controllers.
 * Specifically handles MethodArgumentNotValidException to provide structured
 * validation error feedback to the client.
 */
@RestControllerAdvice
public class ValidationFailed {

    /**
     * Handles validation errors triggered by @Valid or @Validated on request bodies.
     * @param ex - The exception containing validation errors.
     * @return ResponseEntity with a map of field error messages and 400 Bad Request status.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String errorMessage = error.getDefaultMessage();
            // We use "message" as the key as per requirements, 
            // though multiple errors might overwrite this key.
            // If you need all errors, consider using field names as keys.
            errors.put("message", errorMessage);
        });
        
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }
}