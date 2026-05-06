/**
 * doctorServices.js - Service layer for doctor-related API interactions.
 */

import { API_BASE_URL } from "../config/config.js";

const DOCTOR_API = API_BASE_URL + '/doctor';

/**
 * Fetches the list of all doctors.
 * @returns {Promise<Array>} Array of doctors.
 */
export async function getDoctors() {
    try {
        const response = await fetch(DOCTOR_API);
        if (!response.ok) throw new Error("Failed to fetch doctors");
        const data = await response.json();
        return data.doctors || data || [];
    } catch (error) {
        console.error("getDoctors error:", error);
        return [];
    }
}

/**
 * Deletes a specific doctor.
 * @param {string|number} id - The doctor ID.
 * @param {string} token - The authentication token.
 * @returns {Promise<Object>} Object with success status and message.
 */
export async function deleteDoctor(id, token) {
    try {
        const response = await fetch(`${DOCTOR_API}/${id}/${token}`, {
            method: "DELETE"
        });
        const data = await response.json();
        return {
            success: response.ok,
            message: data.message || (response.ok ? "Doctor deleted successfully" : "Failed to delete doctor")
        };
    } catch (error) {
        console.error("deleteDoctor error:", error);
        return { success: false, message: "Error connecting to the server" };
    }
}

/**
 * Saves a new doctor.
 * @param {Object} doctor - The doctor data.
 * @param {string} token - The authentication token.
 * @returns {Promise<Object>} Object with success status and message.
 */
export async function saveDoctor(doctor, token) {
    try {
        const response = await fetch(`${DOCTOR_API}/${token}`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(doctor)
        });
        const data = await response.json();
        return {
            success: response.ok,
            message: data.message || (response.ok ? "Doctor saved successfully" : "Failed to save doctor")
        };
    } catch (error) {
        console.error("saveDoctor error:", error);
        return { success: false, message: "Error connecting to the server" };
    }
}

/**
 * Filters doctors based on name, time, and specialty.
 * @param {string} name - Name fragment.
 * @param {string} time - Time availability (AM/PM).
 * @param {string} specialty - Medical specialty.
 * @returns {Promise<Object>} Object containing the doctors array.
 */
export async function filterDoctors(name, time, specialty) {
    const n = name || "all";
    const t = time || "all";
    const s = specialty || "all";

    try {
        const response = await fetch(`${DOCTOR_API}/filter/${n}/${t}/${s}`);
        if (response.ok) {
            const data = await response.json();
            return data;
        } else {
            console.error("filterDoctors response error");
            return { doctors: [] };
        }
    } catch (error) {
        console.error("filterDoctors unexpected error:", error);
        alert("An error occurred while filtering. Please try again.");
        return { doctors: [] };
    }
}
