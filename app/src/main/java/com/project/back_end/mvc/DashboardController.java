package com.project.back_end.mvc;

import com.project.back_end.services.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

/**
 * DashboardController - MVC Controller for handling Admin and Doctor Dashboard routing.
 */
@Controller
public class DashboardController {

    @Autowired
    private Service service;

    /**
     * Handles HTTP GET requests to the Admin Dashboard.
     * @param token - Admin's authentication token.
     * @return - View name for Thymeleaf to resolve or redirect URL.
     */
    @GetMapping("/adminDashboard/{token}")
    public String adminDashboard(@PathVariable String token) {
        // Call validateToken from the service and check the response status
        ResponseEntity<Map<String, String>> result = service.validateToken(token, "admin");

        // If the response is OK, the token is valid
        if (result.getStatusCode().is2xxSuccessful()) {
            return "admin/adminDashboard";
        }

        // If not valid, redirect to the login/home page
        return "redirect:/";
    }

    /**
     * Handles HTTP GET requests to the Doctor Dashboard.
     * @param token - Doctor's authentication token.
     * @return - View name for Thymeleaf to resolve or redirect URL.
     */
    @GetMapping("/doctorDashboard/{token}")
    public String doctorDashboard(@PathVariable String token) {
        // Call validateToken from the service and check the response status
        ResponseEntity<Map<String, String>> result = service.validateToken(token, "doctor");

        // If the response is OK, the token is valid
        if (result.getStatusCode().is2xxSuccessful()) {
            return "doctor/doctorDashboard";
        }

        // If not valid, redirect to the login/home page
        return "redirect:/";
    }
}
