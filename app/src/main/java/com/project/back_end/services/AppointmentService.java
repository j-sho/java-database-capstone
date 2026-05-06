package com.project.back_end.services;

import com.project.back_end.DTO.AppointmentDTO;
import com.project.back_end.models.Appointment;
import com.project.back_end.models.Doctor;
import com.project.back_end.models.Patient;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.DoctorRepository;
import com.project.back_end.repo.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * AppointmentService - Handles business logic for appointment booking, 
 * updating, canceling, and retrieving appointments with token-based authorization.
 */
@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final TokenService tokenService;
    private final com.project.back_end.services.Service sharedService;

    @Autowired
    public AppointmentService(AppointmentRepository appointmentRepository,
                               PatientRepository patientRepository,
                               DoctorRepository doctorRepository,
                               TokenService tokenService,
                               com.project.back_end.services.Service sharedService) {
        this.appointmentRepository = appointmentRepository;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
        this.tokenService = tokenService;
        this.sharedService = sharedService;
    }

    /**
     * Books a new appointment.
     * @return 1 if successful, 0 if failure.
     */
    @Transactional
    public int bookAppointment(Appointment appointment) {
        try {
            appointmentRepository.save(appointment);
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Updates an existing appointment.
     */
    @Transactional
    public ResponseEntity<Map<String, String>> updateAppointment(Appointment appointment) {
        Map<String, String> response = new HashMap<>();
        Optional<Appointment> existingOpt = appointmentRepository.findById(appointment.getId());

        if (existingOpt.isEmpty()) {
            response.put("message", "Appointment not found.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        // Validate update using shared service logic
        int validity = sharedService.validateAppointment(appointment);
        
        if (validity != 1) {
            response.put("message", "The selected update is invalid or the slot is taken.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        Appointment existing = existingOpt.get();
        existing.setAppointmentTime(appointment.getAppointmentTime());
        existing.setDoctor(appointment.getDoctor());
        existing.setStatus(0); // Reset to scheduled

        appointmentRepository.save(existing);
        response.put("message", "Appointment updated successfully.");
        return ResponseEntity.ok(response);
    }

    /**
     * Cancels an appointment based on ID and token (verifying ownership).
     */
    @Transactional
    public ResponseEntity<Map<String, String>> cancelAppointment(long id, String token) {
        Map<String, String> response = new HashMap<>();
        String identifier = tokenService.extractIdentifier(token);
        Patient patient = patientRepository.findByEmail(identifier);
        
        Optional<Appointment> appointmentOpt = appointmentRepository.findById(id);

        if (appointmentOpt.isEmpty()) {
            response.put("message", "Appointment not found.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        Appointment appointment = appointmentOpt.get();

        // Ensure the patient attempting to cancel is the one who booked it
        if (patient == null || !appointment.getPatient().getId().equals(patient.getId())) {
            response.put("message", "Unauthorized to cancel this appointment.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        appointmentRepository.delete(appointment);
        response.put("message", "Appointment canceled successfully.");
        return ResponseEntity.ok(response);
    }

    /**
     * Updates the status of an appointment.
     */
    @Transactional
    public void changeStatus(int status, long id) {
        Optional<Appointment> appointmentOpt = appointmentRepository.findById(id);
        if (appointmentOpt.isPresent()) {
            Appointment appointment = appointmentOpt.get();
            appointment.setStatus(status);
            appointmentRepository.save(appointment);
        }
    }

    /**
     * Retrieves a list of appointments for a specific doctor on a specific date.
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getAppointment(String pname, LocalDate date, String token) {
        Map<String, Object> result = new HashMap<>();
        String identifier = tokenService.extractIdentifier(token);
        Doctor doctor = doctorRepository.findByEmail(identifier);

        if (doctor == null) {
            result.put("appointments", List.of());
            return result;
        }

        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

        List<Appointment> appointments;
        if (pname == null || pname.equals("null") || pname.isEmpty()) {
            appointments = appointmentRepository.findByDoctorIdAndAppointmentTimeBetween(doctor.getId(), startOfDay, endOfDay);
        } else {
            appointments = appointmentRepository.findByDoctorIdAndPatient_NameContainingIgnoreCaseAndAppointmentTimeBetween(
                    doctor.getId(), pname, startOfDay, endOfDay);
        }

        List<AppointmentDTO> dtos = appointments.stream()
                .map(a -> new AppointmentDTO(
                        a.getId(),
                        a.getDoctor().getId(),
                        a.getDoctor().getName(),
                        a.getPatient().getId(),
                        a.getPatient().getName(),
                        a.getPatient().getEmail(),
                        a.getPatient().getPhone(),
                        null,
                        a.getAppointmentTime(),
                        a.getStatus()
                ))
                .collect(Collectors.toList());

        result.put("appointments", dtos);
        return result;
    }
}
