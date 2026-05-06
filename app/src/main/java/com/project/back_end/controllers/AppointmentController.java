package com.project.back_end.controllers;

import com.project.back_end.models.Appointment;
import com.project.back_end.services.AppointmentService;
import com.project.back_end.services.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

/**
 * AppointmentController - Handles REST endpoints for appointment lifecycle management.
 */
@RestController
@RequestMapping("/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final Service service;

    @Autowired
    public AppointmentController(AppointmentService appointmentService, Service service) {
        this.appointmentService = appointmentService;
        this.service = service;
    }

    /**
     * Retrieves appointments for a specific doctor and date.
     */
    @GetMapping("/{date}/{patientName}/{token}")
    public ResponseEntity<Map<String, Object>> getAppointments(@PathVariable String date,
                                                              @PathVariable String patientName,
                                                              @PathVariable String token) {
        // Validate token for doctor role
        ResponseEntity<Map<String, String>> tokenValidation = service.validateToken(token, "doctor");
        if (tokenValidation.getStatusCode().isError()) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", tokenValidation.getBody().get("error"));
            return ResponseEntity.status(tokenValidation.getStatusCode()).body(errorResponse);
        }

        LocalDate localDate = LocalDate.parse(date);
        Map<String, Object> result = appointmentService.getAppointment(patientName, localDate, token);
        return ResponseEntity.ok(result);
    }

    /**
     * Books a new appointment for a patient.
     */
    @PostMapping("/{token}")
    public ResponseEntity<Map<String, String>> bookAppointment(@RequestBody Appointment appointment,
                                                              @PathVariable String token) {
        Map<String, String> response = new HashMap<>();
        
        // Validate token for patient role
        ResponseEntity<Map<String, String>> tokenValidation = service.validateToken(token, "patient");
        if (tokenValidation.getStatusCode().isError()) {
            return tokenValidation;
        }

        // Validate appointment details
        int validity = service.validateAppointment(appointment);
        if (validity == 1) {
            int result = appointmentService.bookAppointment(appointment);
            if (result == 1) {
                response.put("message", "Appointment booked successfully.");
                return ResponseEntity.status(HttpStatus.CREATED).body(response);
            } else {
                response.put("message", "Error occurred while booking.");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }
        } else if (validity == -1) {
            response.put("message", "Doctor not found.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } else {
            response.put("message", "Selected time slot is unavailable.");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        }
    }

    /**
     * Updates an existing appointment.
     */
    @PutMapping("/{token}")
    public ResponseEntity<Map<String, String>> updateAppointment(@RequestBody Appointment appointment,
                                                                @PathVariable String token) {
        ResponseEntity<Map<String, String>> tokenValidation = service.validateToken(token, "patient");
        if (tokenValidation.getStatusCode().isError()) {
            return tokenValidation;
        }

        return appointmentService.updateAppointment(appointment);
    }

    /**
     * Cancels an appointment.
     */
    @DeleteMapping("/{id}/{token}")
    public ResponseEntity<Map<String, String>> cancelAppointment(@PathVariable long id,
                                                                @PathVariable String token) {
        ResponseEntity<Map<String, String>> tokenValidation = service.validateToken(token, "patient");
        if (tokenValidation.getStatusCode().isError()) {
            return tokenValidation;
        }

        return appointmentService.cancelAppointment(id, token);
    }
}
