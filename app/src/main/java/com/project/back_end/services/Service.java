package com.project.back_end.services;

import com.project.back_end.DTO.Login;
import com.project.back_end.models.Admin;
import com.project.back_end.models.Appointment;
import com.project.back_end.models.Doctor;
import com.project.back_end.models.Patient;
import com.project.back_end.repo.AdminRepository;
import com.project.back_end.repo.DoctorRepository;
import com.project.back_end.repo.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;

/**
 * Service - Central shared service class for authentication, 
 * staff management orchestration, and cross-entity validation.
 */
@org.springframework.stereotype.Service
public class Service {

    private final TokenService tokenService;
    private final AdminRepository adminRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final DoctorService doctorService;
    private final PatientService patientService;

    @Autowired
    public Service(TokenService tokenService,
                   AdminRepository adminRepository,
                   DoctorRepository doctorRepository,
                   PatientRepository patientRepository,
                   DoctorService doctorService,
                   PatientService patientService) {
        this.tokenService = tokenService;
        this.adminRepository = adminRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
        this.doctorService = doctorService;
        this.patientService = patientService;
    }

    /**
     * Validates a token for a given user role.
     */
    public ResponseEntity<Map<String, String>> validateToken(String token, String user) {
        Map<String, String> response = new HashMap<>();
        if (!tokenService.validateToken(token, user)) {
            response.put("error", "Unauthorized: Invalid or expired token");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
        return ResponseEntity.ok(new HashMap<>()); // Returns empty map if valid
    }

    /**
     * Validates admin login credentials.
     */
    @Transactional(readOnly = true)
    public ResponseEntity<Map<String, String>> validateAdmin(Admin receivedAdmin) {
        Map<String, String> response = new HashMap<>();
        Admin admin = adminRepository.findByUsername(receivedAdmin.getUsername());
        
        if (admin != null && admin.getPassword().equals(receivedAdmin.getPassword())) {
            String token = tokenService.generateToken(receivedAdmin.getUsername());
            response.put("token", token);
            return ResponseEntity.ok(response);
        }
        
        response.put("error", "Invalid credentials");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    /**
     * Filters doctors by name, specialty, and availability time (AM/PM).
     */
    @Transactional(readOnly = true)
    public Map<String, Object> filterDoctor(String name, String specialty, String time) {
        if (name != null && specialty != null && time != null) {
            return doctorService.filterDoctorsByNameSpecilityandTime(name, specialty, time);
        } else if (name != null && time != null) {
            return doctorService.filterDoctorByNameAndTime(name, time);
        } else if (name != null && specialty != null) {
            return doctorService.filterDoctorByNameAndSpecility(name, specialty);
        } else if (specialty != null && time != null) {
            return doctorService.filterDoctorByTimeAndSpecility(specialty, time);
        } else if (name != null) {
            return doctorService.findDoctorByName(name);
        } else if (specialty != null) {
            return doctorService.filterDoctorBySpecility(specialty);
        } else if (time != null) {
            return doctorService.filterDoctorsByTime(time);
        } else {
            Map<String, Object> result = new HashMap<>();
            result.put("doctors", doctorService.getDoctors());
            return result;
        }
    }

    /**
     * Validates appointment availability.
     * 1 = Valid, 0 = Unavailable, -1 = Doctor doesn't exist.
     */
    @Transactional(readOnly = true)
    public int validateAppointment(Appointment appointment) {
        Long doctorId = appointment.getDoctor().getId();
        if (!doctorRepository.existsById(doctorId)) {
            return -1;
        }

        LocalDate date = appointment.getAppointmentTime().toLocalDate();
        List<String> availableSlots = doctorService.getDoctorAvailability(doctorId, date);
        
        String requestedTime = appointment.getAppointmentTime().toLocalTime().toString();
        return availableSlots.contains(requestedTime) ? 1 : 0;
    }

    /**
     * Checks if a patient exists by email or phone.
     * true if NOT exists, false if exists.
     */
    @Transactional(readOnly = true)
    public boolean validatePatient(Patient patient) {
        Patient existing = patientRepository.findByEmailOrPhone(patient.getEmail(), patient.getPhone());
        return existing == null;
    }

    /**
     * Validates patient login credentials.
     */
    @Transactional(readOnly = true)
    public ResponseEntity<Map<String, String>> validatePatientLogin(Login login) {
        Map<String, String> response = new HashMap<>();
        Patient patient = patientRepository.findByEmail(login.getIdentifier());
        
        if (patient != null && patient.getPassword().equals(login.getPassword())) {
            String token = tokenService.generateToken(login.getIdentifier());
            response.put("token", token);
            return ResponseEntity.ok(response);
        }
        
        response.put("error", "Invalid credentials");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    /**
     * Filters patient appointments based on criteria.
     */
    @Transactional(readOnly = true)
    public ResponseEntity<Map<String, Object>> filterPatient(String condition, String name, String token) {
        String identifier = tokenService.extractIdentifier(token);
        Patient patient = patientRepository.findByEmail(identifier);
        
        if (patient == null) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Patient not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }

        if (condition != null && name != null) {
            return patientService.filterByDoctorAndCondition(condition, name, patient.getId());
        } else if (condition != null) {
            return patientService.filterByCondition(condition, patient.getId());
        } else if (name != null) {
            return patientService.filterByDoctor(name, patient.getId());
        } else {
            return patientService.getPatientAppointment(patient.getId(), token);
        }
    }
}
