package com.project.back_end.controllers;

import com.project.back_end.models.Admin;
import com.project.back_end.services.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * AdminController - Handles REST endpoints for administrative operations,
 * specifically login and authentication.
 */
@RestController
@RequestMapping("${api.path}admin")
public class AdminController {

    private final Service service;

    /**
     * Constructor injection for the Service layer.
     */
    @Autowired
    public AdminController(Service service) {
        this.service = service;
    }

    /**
     * Handles admin login requests.
     * @param admin - The login credentials from the request body.
     * @return ResponseEntity containing a token on success or an error message on failure.
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> adminLogin(@RequestBody Admin admin) {
        return service.validateAdmin(admin);
    }
}
