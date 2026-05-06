/**
 * services/index.js - Entry point for role-based login handling and modal triggers.
 */

import { openModal } from "../components/modals.js";
import { API_BASE_URL } from "../config/config.js";
import { selectRole } from "../render.js";

// API Endpoints
const ADMIN_API = API_BASE_URL + '/admin';
const DOCTOR_API = API_BASE_URL + '/doctor/login';

/**
 * Initialize event listeners for the main role selection screen.
 */
window.onload = function () {
    const adminBtn = document.getElementById('adminLogin');
    const doctorBtn = document.getElementById('doctorLogin');

    if (adminBtn) {
        adminBtn.addEventListener('click', () => {
            openModal('adminLogin');
        });
    }

    if (doctorBtn) {
        doctorBtn.addEventListener('click', () => {
            openModal('doctorLogin');
        });
    }
};

/**
 * Handles the Admin login process.
 */
window.adminLoginHandler = async function () {
    const usernameInput = document.getElementById("adminUsername");
    const passwordInput = document.getElementById("adminPassword");

    if (!usernameInput || !passwordInput) return;

    const username = usernameInput.value;
    const password = passwordInput.value;
    const admin = { username, password };

    try {
        const response = await fetch(ADMIN_API, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(admin)
        });

        if (response.ok) {
            const data = await response.json();
            const token = data.token;
            localStorage.setItem("token", token);
            selectRole("admin");
        } else {
            alert("Invalid credentials!");
        }
    } catch (error) {
        console.error("Admin login error:", error);
        alert("An unexpected error occurred. Please try again.");
    }
};

/**
 * Handles the Doctor login process.
 */
window.doctorLoginHandler = async function () {
    const emailInput = document.getElementById("doctorEmail");
    const passwordInput = document.getElementById("doctorPassword");

    if (!emailInput || !passwordInput) return;

    const email = emailInput.value;
    const password = passwordInput.value;
    const doctor = { email, password };

    try {
        const response = await fetch(DOCTOR_API, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(doctor)
        });

        if (response.ok) {
            const data = await response.json();
            const token = data.token;
            localStorage.setItem("token", token);
            selectRole("doctor");
        } else {
            alert("Invalid credentials!");
        }
    } catch (error) {
        console.error("Doctor login error:", error);
        alert("An unexpected error occurred. Please try again.");
    }
};
