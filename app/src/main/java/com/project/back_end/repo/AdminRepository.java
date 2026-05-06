package com.project.back_end.repo;

import com.project.back_end.models.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * AdminRepository - Data access layer for the Admin entity.
 * Provides basic CRUD operations and custom query methods for Admins.
 */
@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {

    /**
     * Finds an Admin entity by its username.
     * @param username - The username to search for.
     * @return - The Admin entity if found, otherwise null.
     */
    Admin findByUsername(String username);
}
