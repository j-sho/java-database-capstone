package com.project.back_end.services;

import com.project.back_end.DTO.Login;
import com.project.back_end.models.Appointment;
import com.project.back_end.models.Doctor;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.DoctorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * DoctorService - Handles business logic for Doctor management, filtering, and authentication.
 */
@Service
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final AppointmentRepository appointmentRepository;
    private final TokenService tokenService;

    @Autowired
    public DoctorService(DoctorRepository doctorRepository,
                         AppointmentRepository appointmentRepository,
                         TokenService tokenService) {
        this.doctorRepository = doctorRepository;
        this.appointmentRepository = appointmentRepository;
        this.tokenService = tokenService;
    }

    /**
     * Fetches available time slots for a specific doctor on a given date.
     */
    @Transactional(readOnly = true)
    public List<String> getDoctorAvailability(Long doctorId, LocalDate date) {
        Optional<Doctor> doctorOpt = doctorRepository.findById(doctorId);
        if (doctorOpt.isEmpty()) return Collections.emptyList();

        Doctor doctor = doctorOpt.get();
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

        List<Appointment> bookedAppointments = appointmentRepository.findByDoctorIdAndAppointmentTimeBetween(doctorId, startOfDay, endOfDay);
        
        Set<String> bookedTimes = bookedAppointments.stream()
                .map(a -> a.getAppointmentTime().toLocalTime().toString())
                .collect(Collectors.toSet());

        return doctor.getAvailableTimes().stream()
                .filter(time -> !bookedTimes.contains(time))
                .collect(Collectors.toList());
    }

    /**
     * Saves a new doctor. Returns 1 for success, -1 for conflict, 0 for error.
     */
    @Transactional
    public int saveDoctor(Doctor doctor) {
        try {
            if (doctorRepository.findByEmail(doctor.getEmail()) != null) {
                return -1; // Already exists
            }
            doctorRepository.save(doctor);
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Updates doctor details. Returns 1 for success, -1 for not found, 0 for error.
     */
    @Transactional
    public int updateDoctor(Doctor doctor) {
        if (doctor.getId() == null || !doctorRepository.existsById(doctor.getId())) {
            return -1;
        }
        try {
            doctorRepository.save(doctor);
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Retrieves all doctors.
     */
    @Transactional(readOnly = true)
    public List<Doctor> getDoctors() {
        return doctorRepository.findAll();
    }

    /**
     * Deletes a doctor and all associated appointments.
     */
    @Transactional
    public int deleteDoctor(long id) {
        if (!doctorRepository.existsById(id)) {
            return -1;
        }
        try {
            appointmentRepository.deleteAllByDoctorId(id);
            doctorRepository.deleteById(id);
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Validates doctor's login credentials.
     */
    @Transactional(readOnly = true)
    public ResponseEntity<Map<String, String>> validateDoctor(Login login) {
        Map<String, String> response = new HashMap<>();
        Doctor doctor = doctorRepository.findByEmail(login.getIdentifier());

        if (doctor == null || !doctor.getPassword().equals(login.getPassword())) {
            response.put("message", "Invalid email or password.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        String token = tokenService.generateToken(login.getIdentifier());
        response.put("token", token);
        response.put("message", "Login successful");
        return ResponseEntity.ok(response);
    }

    /**
     * Finds doctors by name.
     */
    @Transactional(readOnly = true)
    public Map<String, Object> findDoctorByName(String name) {
        Map<String, Object> result = new HashMap<>();
        result.put("doctors", doctorRepository.findByNameLike(name));
        return result;
    }

    /**
     * Filters doctors by name, specialty, and AM/PM availability.
     */
    @Transactional(readOnly = true)
    public Map<String, Object> filterDoctorsByNameSpecilityandTime(String name, String specialty, String amOrPm) {
        List<Doctor> doctors = doctorRepository.findByNameContainingIgnoreCaseAndSpecialtyIgnoreCase(name, specialty);
        Map<String, Object> result = new HashMap<>();
        result.put("doctors", filterDoctorByTime(doctors, amOrPm));
        return result;
    }

    /**
     * Filters doctors by name and AM/PM availability.
     */
    @Transactional(readOnly = true)
    public Map<String, Object> filterDoctorByNameAndTime(String name, String amOrPm) {
        List<Doctor> doctors = doctorRepository.findByNameLike(name);
        Map<String, Object> result = new HashMap<>();
        result.put("doctors", filterDoctorByTime(doctors, amOrPm));
        return result;
    }

    /**
     * Filters doctors by name and specialty.
     */
    @Transactional(readOnly = true)
    public Map<String, Object> filterDoctorByNameAndSpecility(String name, String specialty) {
        Map<String, Object> result = new HashMap<>();
        result.put("doctors", doctorRepository.findByNameContainingIgnoreCaseAndSpecialtyIgnoreCase(name, specialty));
        return result;
    }

    /**
     * Filters doctors by specialty and AM/PM availability.
     */
    @Transactional(readOnly = true)
    public Map<String, Object> filterDoctorByTimeAndSpecility(String specialty, String amOrPm) {
        List<Doctor> doctors = doctorRepository.findBySpecialtyIgnoreCase(specialty);
        Map<String, Object> result = new HashMap<>();
        result.put("doctors", filterDoctorByTime(doctors, amOrPm));
        return result;
    }

    /**
     * Filters doctors by specialty.
     */
    @Transactional(readOnly = true)
    public Map<String, Object> filterDoctorBySpecility(String specialty) {
        Map<String, Object> result = new HashMap<>();
        result.put("doctors", doctorRepository.findBySpecialtyIgnoreCase(specialty));
        return result;
    }

    /**
     * Filters all doctors by AM/PM availability.
     */
    @Transactional(readOnly = true)
    public Map<String, Object> filterDoctorsByTime(String amOrPm) {
        List<Doctor> doctors = doctorRepository.findAll();
        Map<String, Object> result = new HashMap<>();
        result.put("doctors", filterDoctorByTime(doctors, amOrPm));
        return result;
    }

    /**
     * Private helper to filter a list of doctors by their available times (AM/PM).
     */
    private List<Doctor> filterDoctorByTime(List<Doctor> doctors, String amOrPm) {
        if (amOrPm == null || amOrPm.equalsIgnoreCase("null") || amOrPm.isEmpty()) return doctors;

        boolean isAM = amOrPm.equalsIgnoreCase("AM");
        return doctors.stream()
                .filter(d -> d.getAvailableTimes().stream().anyMatch(t -> {
                    LocalTime lt = LocalTime.parse(t);
                    return isAM ? lt.isBefore(LocalTime.NOON) : !lt.isBefore(LocalTime.NOON);
                }))
                .collect(Collectors.toList());
    }
}
