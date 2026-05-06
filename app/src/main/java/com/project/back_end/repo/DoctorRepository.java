package com.project.back_end.repo;

import com.project.back_end.models.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * DoctorRepository - Data access layer for the Doctor entity.
 */
@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {

    /**
     * Finds a doctor by their email address.
     * @param email - The email to search for.
     * @return - The Doctor entity if found.
     */
    Doctor findByEmail(String email);

    /**
     * Finds doctors by partial name match.
     * Uses @Query with LIKE and CONCAT for flexible pattern matching.
     */
    @Query("SELECT d FROM Doctor d WHERE d.name LIKE CONCAT('%', :name, '%')")
    List<Doctor> findByNameLike(@Param("name") String name);

    /**
     * Filters doctors by partial name and exact specialty (case-insensitive).
     * Uses @Query with LOWER, CONCAT, and LIKE for robust matching.
     */
    @Query("SELECT d FROM Doctor d WHERE LOWER(d.name) LIKE LOWER(CONCAT('%', :name, '%')) AND LOWER(d.specialty) = LOWER(:specialty)")
    List<Doctor> findByNameContainingIgnoreCaseAndSpecialtyIgnoreCase(@Param("name") String name, @Param("specialty") String specialty);

    /**
     * Finds doctors by specialty, ignoring case sensitivity.
     */
    List<Doctor> findBySpecialtyIgnoreCase(String specialty);
}