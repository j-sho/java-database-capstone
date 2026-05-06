/**
 * patientServices.js - Service layer for patient-related API interactions.
 */

import { API_BASE_URL } from "../config/config.js";

// Base Patient API Endpoint
const PATIENT_API = API_BASE_URL + '/patient';

/**
 * Handles patient registration.
 * @param {Object} data - Patient details (name, email, password, etc.).
 * @returns {Promise<Object>} Success status and message.
 */
export async function patientSignup(data) {
    try {
        // Send a POST request to the base patient endpoint for signup
        const response = await fetch(`${PATIENT_API}`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(data)
        });

        const result = await response.json();
        
        if (!response.ok) {
            throw new Error(result.message || "Signup failed");
        }

        return { success: true, message: result.message };
    } catch (error) {
        console.error("Error :: patientSignup :: ", error);
        return { success: false, message: error.message };
    }
}

/**
 * Handles patient login authentication.
 * @param {Object} data - Login credentials (email and password).
 * @returns {Promise<Response>} The raw fetch response.
 */
export async function patientLogin(data) {
    // Log input data for development purposes
    console.log("patientLogin :: ", data);
    
    // Send POST request to the login endpoint
    return await fetch(`${PATIENT_API}/login`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(data)
    });
}

/**
 * Fetches data for the currently logged-in patient.
 * @param {string} token - Authentication token from localStorage.
 * @returns {Promise<Object|null>} Patient object or null on failure.
 */
export async function getPatientData(token) {
    try {
        // Send GET request with token in the path to retrieve details
        const response = await fetch(`${PATIENT_API}/${token}`);
        const data = await response.json();
        
        if (response.ok) {
            return data.patient || data;
        }
        return null;
    } catch (error) {
        console.error("Error fetching patient details:", error);
        return null;
    }
}

/**
 * Fetches appointments for a patient, accessible by both patients and doctors.
 * @param {string|number} id - Patient's unique ID.
 * @param {string} token - Authentication token.
 * @param {string} user - Requester role ('patient' or 'doctor').
 * @returns {Promise<Array|null>} Appointments array or null on failure.
 */
export async function getPatientAppointments(id, token, user) {
    try {
        // Dynamic URL supporting role-based behavior on the backend
        const response = await fetch(`${PATIENT_API}/${id}/${user}/${token}`);
        const data = await response.json();
        
        if (response.ok) {
            return data.appointments;
        }
        return null;
    } catch (error) {
        console.error("Error fetching patient appointments:", error);
        return null;
    }
}

/**
 * Filters patient appointments based on condition and name.
 * @param {string} condition - Appointment status (e.g., 'pending', 'consulted').
 * @param {string} name - Filter name.
 * @param {string} token - Authentication token.
 * @returns {Promise<Object>} Object containing the appointments array.
 */
export async function filterAppointments(condition, name, token) {
    try {
        // Send GET request to the filtered endpoint with path parameters
        const response = await fetch(`${PATIENT_API}/filter/${condition}/${name}/${token}`);

        if (response.ok) {
            const data = await response.json();
            return data;
        } else {
            console.error("Failed to filter appointments:", response.statusText);
            return { appointments: [] };
        }
    } catch (error) {
        console.error("Error in filterAppointments:", error);
        alert("Something went wrong while filtering appointments!");
        return { appointments: [] };
    }
}
