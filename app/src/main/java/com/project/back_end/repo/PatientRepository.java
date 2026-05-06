package com.project.back_end.repo;

import com.project.back_end.models.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * PatientRepository - Data access layer for the Patient entity.
 */
@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {

    /**
     * Finds a patient by their email address.
     * @param email - The email to search for.
     * @return - The Patient entity if found.
     */
    Patient findByEmail(String email);

    /**
     * Finds a patient by either their email or phone number.
     * This is useful for registration checks or alternative login methods.
     * @param email - The email to search for.
     * @param phone - The phone number to search for.
     * @return - The Patient entity if a match is found for either field.
     */
    Patient findByEmailOrPhone(String email, String phone);
}
