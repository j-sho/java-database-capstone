/**
 * doctorCard.js - Component for creating and rendering individual doctor cards.
 */

// Import necessary services and utilities
import { deleteDoctor } from "../services/doctorServices.js";
import { getPatientData } from "../services/patientServices.js";

/**
 * Creates a DOM element representing a doctor card.
 * @param {Object} doctor - The doctor object containing details.
 * @returns {HTMLElement} The completed doctor card element.
 */
export function createDoctorCard(doctor) {
    // 1. Create the main card container
    const card = document.createElement("div");
    card.classList.add("doctor-card");

    // 2. Fetch the User’s Role
    const role = localStorage.getItem("userRole");

    // 3. Create Doctor Info Section
    const infoDiv = document.createElement("div");
    infoDiv.classList.add("doctor-info");

    const name = document.createElement("h3");
    name.textContent = doctor.name;

    const specialization = document.createElement("p");
    specialization.textContent = `Specialty: ${doctor.specialty || doctor.specialization}`;

    const email = document.createElement("p");
    email.textContent = `Email: ${doctor.email}`;

    const availability = document.createElement("p");
    const times = Array.isArray(doctor.availableTimes) ? doctor.availableTimes.join(", ") : doctor.availableTimes;
    availability.textContent = `Available: ${times || "Not specified"}`;

    infoDiv.appendChild(name);
    infoDiv.appendChild(specialization);
    infoDiv.appendChild(email);
    infoDiv.appendChild(availability);

    // 4. Create Button Container
    const actionsDiv = document.createElement("div");
    actionsDiv.classList.add("card-actions");

    // 5. Conditionally Add Buttons Based on Role
    if (role === "admin") {
        // === ADMIN ROLE ACTIONS ===
        const removeBtn = document.createElement("button");
        removeBtn.textContent = "Delete";
        removeBtn.classList.add("adminBtn"); // Using the class from CSS
        
        removeBtn.addEventListener("click", async () => {
            if (confirm(`Are you sure you want to delete Dr. ${doctor.name}?`)) {
                const token = localStorage.getItem("token");
                try {
                    const success = await deleteDoctor(doctor.id, token);
                    if (success) {
                        card.remove();
                    }
                } catch (error) {
                    console.error("Failed to delete doctor:", error);
                }
            }
        });
        actionsDiv.appendChild(removeBtn);

    } else if (role === "patient") {
        // === PATIENT (NOT LOGGED-IN) ROLE ACTIONS ===
        const bookNow = document.createElement("button");
        bookNow.textContent = "Book Now";
        bookNow.addEventListener("click", () => {
            alert("Patient needs to login first.");
        });
        actionsDiv.appendChild(bookNow);

    } else if (role === "loggedPatient") {
        // === LOGGED-IN PATIENT ROLE ACTIONS ===
        const bookNow = document.createElement("button");
        bookNow.textContent = "Book Now";
        bookNow.addEventListener("click", async (e) => {
            const token = localStorage.getItem("token");
            if (!token) {
                alert("Session expired. Please log in again.");
                window.location.href = "/";
                return;
            }
            try {
                const patientData = await getPatientData(token);
                // The showBookingOverlay function is assumed to be available globally or imported
                if (typeof showBookingOverlay === "function") {
                    showBookingOverlay(e, doctor, patientData);
                } else {
                    console.error("showBookingOverlay is not defined.");
                }
            } catch (error) {
                console.error("Failed to fetch patient details:", error);
            }
        });
        actionsDiv.appendChild(bookNow);
    }

    // 6. Final Assembly
    card.appendChild(infoDiv);
    card.appendChild(actionsDiv);

    return card;
}
