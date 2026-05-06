package com.project.back_end.repo;

import com.project.back_end.models.Prescription;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * PrescriptionRepository - Data access layer for the Prescription document (MongoDB).
 * Extends MongoRepository to provide standard CRUD operations for document-based data.
 */
@Repository
public interface PrescriptionRepository extends MongoRepository<Prescription, String> {

    /**
     * Finds all prescriptions associated with a specific appointment ID.
     * @param appointmentId - The ID of the appointment.
     * @return - A list of prescriptions for the given appointment.
     */
    List<Prescription> findByAppointmentId(Long appointmentId);
}
