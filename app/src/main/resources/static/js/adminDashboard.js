/**
 * adminDashboard.js - Logic for the Admin Dashboard.
 */

import { openModal, closeModal } from "./components/modals.js";
import { getDoctors, filterDoctors, saveDoctor } from "./services/doctorServices.js";
import { createDoctorCard } from "./components/doctorCard.js";

// Event Binding for Page Load
document.addEventListener("DOMContentLoaded", () => {
    // Initial load of doctor cards
    loadDoctorCards();

    // Attach click listener to Add Doctor button
    const addDocBtn = document.getElementById("addDocBtn");
    if (addDocBtn) {
        addDocBtn.addEventListener("click", () => {
            openModal("addDoctor");
        });
    }

    // Attach search and filter listeners
    const searchBar = document.getElementById("searchBar");
    const filterTime = document.getElementById("filterTime");
    const filterSpecialty = document.getElementById("filterSpecialty");

    if (searchBar) searchBar.addEventListener("input", filterDoctorsOnChange);
    if (filterTime) filterTime.addEventListener("change", filterDoctorsOnChange);
    if (filterSpecialty) filterSpecialty.addEventListener("change", filterDoctorsOnChange);
});

/**
 * Fetches all doctors and displays them in the dashboard.
 */
export async function loadDoctorCards() {
    try {
        const doctors = await getDoctors();
        renderDoctorCards(doctors);
    } catch (error) {
        console.error("Error fetching doctor list:", error);
    }
}

/**
 * Gathers current filter/search values and renders matching results.
 */
async function filterDoctorsOnChange() {
    const name = document.getElementById("searchBar")?.value || "";
    const time = document.getElementById("filterTime")?.value || "";
    const specialty = document.getElementById("filterSpecialty")?.value || "";

    try {
        const data = await filterDoctors(name, time, specialty);
        const doctors = data.doctors || [];

        if (doctors.length > 0) {
            renderDoctorCards(doctors);
        } else {
            const contentDiv = document.getElementById("content");
            if (contentDiv) {
                contentDiv.innerHTML = `<p class="no-results">No doctors found.</p>`;
            }
        }
    } catch (error) {
        console.error("Filtering error:", error);
        alert("An error occurred while filtering doctors.");
    }
}

/**
 * Utility function to render a list of doctor cards.
 * @param {Array} doctors - List of doctor objects.
 */
function renderDoctorCards(doctors) {
    const contentDiv = document.getElementById("content");
    if (!contentDiv) return;

    contentDiv.innerHTML = ""; // Clear existing content

    doctors.forEach(doctor => {
        const card = createDoctorCard(doctor);
        contentDiv.appendChild(card);
    });
}

/**
 * Collects form data from the "Add Doctor" modal and saves it via the service.
 */
window.adminAddDoctor = async function () {
    // Collect input values from modal form
    const name = document.getElementById("doctorName")?.value;
    const specialty = document.getElementById("specialization")?.value;
    const email = document.getElementById("doctorEmail")?.value;
    const password = document.getElementById("doctorPassword")?.value;
    const mobile = document.getElementById("doctorPhone")?.value;

    // Collect checkbox values for doctor availability
    const availabilityCheckboxes = document.querySelectorAll('input[name="availability"]:checked');
    const availableTimes = Array.from(availabilityCheckboxes).map(cb => cb.value);

    // Verify authentication token
    const token = localStorage.getItem("token");
    if (!token) {
        alert("Session expired or invalid login. Please log in again.");
        window.location.href = "/";
        return;
    }

    // Build doctor object
    const doctor = {
        name,
        specialty,
        email,
        password,
        phone: mobile,
        availableTimes
    };

    try {
        const result = await saveDoctor(doctor, token);
        if (result.success) {
            alert("Doctor added successfully!");
            if (typeof closeModal === "function") {
                // closeModal() should handle UI hiding logic
                const modal = document.getElementById('modal');
                if (modal) modal.style.display = 'none';
            } else {
                const modal = document.getElementById('modal');
                if (modal) modal.style.display = 'none';
            }
            
            // Reload the doctor list to reflect changes
            loadDoctorCards();
        } else {
            alert("Failed to add doctor: " + (result.message || "Unknown error"));
        }
    } catch (error) {
        console.error("Add doctor error:", error);
        alert("An unexpected error occurred while saving the doctor.");
    }
};
