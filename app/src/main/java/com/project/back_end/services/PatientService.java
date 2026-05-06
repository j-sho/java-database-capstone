package com.project.back_end.services;

import com.project.back_end.DTO.AppointmentDTO;
import com.project.back_end.models.Appointment;
import com.project.back_end.models.Patient;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * PatientService - Handles business logic for patient management with built-in
 * identity verification and structured API responses.
 */
@Service
public class PatientService {

    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;
    private final TokenService tokenService;

    @Autowired
    public PatientService(PatientRepository patientRepository,
                          AppointmentRepository appointmentRepository,
                          TokenService tokenService) {
        this.patientRepository = patientRepository;
        this.appointmentRepository = appointmentRepository;
        this.tokenService = tokenService;
    }

    /**
     * Saves a new patient to the database.
     * @return 1 on success, 0 on failure.
     */
    @Transactional
    public int createPatient(Patient patient) {
        try {
            patientRepository.save(patient);
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Retrieves appointments for a patient after verifying token identity.
     */
    @Transactional(readOnly = true)
    public ResponseEntity<Map<String, Object>> getPatientAppointment(Long id, String token) {
        Map<String, Object> response = new HashMap<>();
        String identifier = tokenService.extractIdentifier(token);
        Patient patient = patientRepository.findByEmail(identifier);

        if (patient == null || !patient.getId().equals(id)) {
            response.put("message", "Unauthorized access to patient data.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        List<Appointment> appointments = appointmentRepository.findByPatientId(id);
        response.put("appointments", convertToDTOList(appointments));
        return ResponseEntity.ok(response);
    }

    /**
     * Filters appointments by condition (past or future).
     */
    @Transactional(readOnly = true)
    public ResponseEntity<Map<String, Object>> filterByCondition(String condition, Long id) {
        Map<String, Object> response = new HashMap<>();
        int status = condition.equalsIgnoreCase("past") ? 1 : (condition.equalsIgnoreCase("future") ? 0 : -1);
        
        if (status == -1) {
            response.put("message", "Invalid condition provided.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        List<Appointment> appointments = appointmentRepository.findByPatient_IdAndStatusOrderByAppointmentTimeAsc(id, status);
        response.put("appointments", convertToDTOList(appointments));
        return ResponseEntity.ok(response);
    }

    /**
     * Filters patient's appointments by doctor's name.
     */
    @Transactional(readOnly = true)
    public ResponseEntity<Map<String, Object>> filterByDoctor(String name, Long patientId) {
        Map<String, Object> response = new HashMap<>();
        List<Appointment> appointments = appointmentRepository.filterByDoctorNameAndPatientId(name, patientId);
        response.put("appointments", convertToDTOList(appointments));
        return ResponseEntity.ok(response);
    }

    /**
     * Filters patient's appointments by doctor's name and condition.
     */
    @Transactional(readOnly = true)
    public ResponseEntity<Map<String, Object>> filterByDoctorAndCondition(String condition, String name, long patientId) {
        Map<String, Object> response = new HashMap<>();
        int status = condition.equalsIgnoreCase("past") ? 1 : (condition.equalsIgnoreCase("future") ? 0 : -1);
        
        if (status == -1) {
            response.put("message", "Invalid condition provided.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        List<Appointment> appointments = appointmentRepository.filterByDoctorNameAndPatientIdAndStatus(name, patientId, status);
        response.put("appointments", convertToDTOList(appointments));
        return ResponseEntity.ok(response);
    }

    /**
     * Fetches patient's details based on the provided JWT token.
     */
    @Transactional(readOnly = true)
    public ResponseEntity<Map<String, Object>> getPatientDetails(String token) {
        Map<String, Object> response = new HashMap<>();
        try {
            String identifier = tokenService.extractIdentifier(token);
            Patient patient = patientRepository.findByEmail(identifier);
            if (patient == null) {
                response.put("message", "Patient not found.");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            response.put("patient", patient);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("message", "Invalid token.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }

    /**
     * Private helper to transform Appointment entities into AppointmentDTOs.
     */
    private List<AppointmentDTO> convertToDTOList(List<Appointment> appointments) {
        return appointments.stream()
                .map(a -> new AppointmentDTO(
                        a.getId(),
                        a.getDoctor().getId(),
                        a.getDoctor().getName(),
                        a.getPatient().getId(),
                        a.getPatient().getName(),
                        a.getPatient().getEmail(),
                        a.getPatient().getPhone(),
                        a.getPatient().getAddress(),
                        a.getAppointmentTime(),
                        a.getStatus()
                ))
                .collect(Collectors.toList());
    }
}
