/**
 * patientDashboard.js - Logic for the Patient Dashboard.
 */

import { getDoctors, filterDoctors } from './services/doctorServices.js';
import { openModal } from './components/modals.js';
import { createDoctorCard } from './components/doctorCard.js';
import { patientSignup, patientLogin } from './services/patientServices.js';
// selectRole is global

/**
 * Initialize the dashboard and event listeners on page load.
 */
document.addEventListener("DOMContentLoaded", () => {
    // 1. Load initial doctor cards
    loadDoctorCards();

    // 2. Bind modal triggers for Login and Signup
    const signupBtn = document.getElementById("patientSignup");
    if (signupBtn) {
        signupBtn.addEventListener("click", () => openModal("patientSignup"));
    }

    const loginBtn = document.getElementById("patientLogin");
    if (loginBtn) {
        loginBtn.addEventListener("click", () => openModal("patientLogin"));
    }

    // 3. Set up listeners for Search and Filter Logic
    const searchBar = document.getElementById("searchBar");
    const filterTime = document.getElementById("filterTime");
    const filterSpecialty = document.getElementById("filterSpecialty");

    if (searchBar) searchBar.addEventListener("input", filterDoctorsOnChange);
    if (filterTime) filterTime.addEventListener("change", filterDoctorsOnChange);
    if (filterSpecialty) filterSpecialty.addEventListener("change", filterDoctorsOnChange);
});

/**
 * Fetches all available doctors and renders them as cards.
 */
async function loadDoctorCards() {
    try {
        const doctors = await getDoctors();
        const contentDiv = document.getElementById("content");
        if (!contentDiv) return;

        contentDiv.innerHTML = ""; // Clear existing content

        if (!doctors || doctors.length === 0) {
            contentDiv.innerHTML = "<p>No doctors available at the moment.</p>";
            return;
        }

        doctors.forEach(doctor => {
            const card = createDoctorCard(doctor);
            contentDiv.appendChild(card);
        });
    } catch (error) {
        console.error("Failed to load doctors:", error);
    }
}

/**
 * Gathers values from filters and re-renders the doctor list.
 */
function filterDoctorsOnChange() {
    const searchVal = document.getElementById("searchBar")?.value.trim() || "";
    const timeVal = document.getElementById("filterTime")?.value || "";
    const specialtyVal = document.getElementById("filterSpecialty")?.value || "";

    // Normalize empty strings to null for the API
    const name = searchVal.length > 0 ? searchVal : null;
    const time = timeVal.length > 0 ? timeVal : null;
    const specialty = specialtyVal.length > 0 ? specialtyVal : null;

    filterDoctors(name, time, specialty)
        .then(response => {
            const doctors = response.doctors || [];
            const contentDiv = document.getElementById("content");
            if (!contentDiv) return;

            contentDiv.innerHTML = ""; // Clear existing content

            if (doctors.length > 0) {
                doctors.forEach(doctor => {
                    const card = createDoctorCard(doctor);
                    contentDiv.appendChild(card);
                });
            } else {
                contentDiv.innerHTML = "<p>No doctors found with the given filters.</p>";
            }
        })
        .catch(error => {
            console.error("Failed to filter doctors:", error);
            alert("❌ An error occurred while filtering doctors.");
        });
}

/**
 * Handles patient signup form submission.
 */
window.signupPatient = async function () {
    const name = document.getElementById("patientSignupName")?.value;
    const email = document.getElementById("patientSignupEmail")?.value;
    const password = document.getElementById("patientSignupPassword")?.value;
    const phone = document.getElementById("patientSignupPhone")?.value;
    const address = document.getElementById("patientSignupAddress")?.value;

    const data = { name, email, password, phone, address };

    try {
        const { success, message } = await patientSignup(data);
        if (success) {
            alert(message || "Signup successful!");
            const modal = document.getElementById("modal");
            if (modal) modal.style.display = "none";
            window.location.reload();
        } else {
            alert("❌ Signup failed: " + message);
        }
    } catch (error) {
        console.error("Signup failed:", error);
        alert("❌ An error occurred while signing up.");
    }
};

/**
 * Handles patient login form submission.
 */
window.loginPatient = async function () {
    const identifier = document.getElementById("patientLoginEmail")?.value;
    const password = document.getElementById("patientLoginPassword")?.value;
    const data = { identifier, password };

    try {
        const response = await patientLogin(data);
        
        if (response.ok) {
            const result = await response.json();
            // Store token and redirect
            localStorage.setItem('token', result.token);
            if (typeof window.selectRole === "function") {
                window.selectRole('loggedPatient');
            }
            window.location.href = '/pages/patientDashboard.html'; // Or loggedPatientDashboard.html if required
        } else {
            alert('❌ Invalid credentials!');
        }
    } catch (error) {
        console.error("Login failed:", error);
        alert("❌ Failed to Login. Please check your connection.");
    }
};
