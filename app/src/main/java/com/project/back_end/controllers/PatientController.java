package com.project.back_end.controllers;

import com.project.back_end.DTO.Login;
import com.project.back_end.models.Patient;
import com.project.back_end.services.PatientService;
import com.project.back_end.services.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * PatientController - Handles REST endpoints for patient registration, 
 * authentication, profile management, and appointment history.
 */
@RestController
@RequestMapping("/patient")
public class PatientController {

    private final PatientService patientService;
    private final Service service;

    @Autowired
    public PatientController(PatientService patientService, Service service) {
        this.patientService = patientService;
        this.service = service;
    }

    /**
     * Retrieves patient profile details using a token.
     */
    @GetMapping("/{token:.+}")
    public ResponseEntity<Map<String, Object>> getPatient(@PathVariable String token) {
        ResponseEntity<Map<String, String>> tokenValidation = service.validateToken(token, "patient");
        if (tokenValidation.getStatusCode().isError()) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Unauthorized access.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }

        return patientService.getPatientDetails(token);
    }

    /**
     * Registers a new patient.
     */
    @PostMapping
    public ResponseEntity<Map<String, String>> createPatient(@RequestBody Patient patient) {
        Map<String, String> response = new HashMap<>();
        
        // Check if patient already exists (email or phone)
        boolean isUnique = service.validatePatient(patient);
        if (!isUnique) {
            response.put("message", "Patient with email id or phone no already exist");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        }

        int result = patientService.createPatient(patient);
        if (result == 1) {
            response.put("message", "Signup successful");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } else {
            response.put("message", "Internal server error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Handles patient login.
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody Login login) {
        return service.validatePatientLogin(login);
    }

    /**
     * Fetches appointment history for a specific patient.
     */
    @GetMapping("/{id}/{token:.+}")
    public ResponseEntity<Map<String, Object>> getPatientAppointment(@PathVariable Long id,
                                                                    @PathVariable String token) {
        // Here we validate as "patient" but the service implementation can handle other checks if needed.
        // Based on the prompt, it validates the token for the patient role.
        ResponseEntity<Map<String, String>> tokenValidation = service.validateToken(token, "patient");
        if (tokenValidation.getStatusCode().isError()) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Unauthorized access to appointments.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }

        return patientService.getPatientAppointment(id, token);
    }

    /**
     * Filters patient's appointments based on condition and/or doctor name.
     */
    @GetMapping("/filter/{condition}/{name}/{token:.+}")
    public ResponseEntity<Map<String, Object>> filterPatientAppointment(@PathVariable String condition,
                                                                       @PathVariable String name,
                                                                       @PathVariable String token) {
        ResponseEntity<Map<String, String>> tokenValidation = service.validateToken(token, "patient");
        if (tokenValidation.getStatusCode().isError()) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Unauthorized access.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }

        // Handle "null", "all", or empty strings from frontend
        String filterCondition = (condition == null || condition.equalsIgnoreCase("null") || condition.equalsIgnoreCase("all") || condition.isEmpty()) ? null : condition;
        String filterName = (name == null || name.equalsIgnoreCase("null") || name.equalsIgnoreCase("all") || name.isEmpty()) ? null : name;

        return service.filterPatient(filterCondition, filterName, token);
    }
}
