/**
 * doctorDashboard.js - Logic for the Doctor Dashboard.
 */

import { getAllAppointments } from "./services/appointmentRecordService.js";
import { createPatientRow } from "./components/patientRows.js";

// Global Variables
let selectedDate = new Date().toISOString().split('T')[0];
let patientName = "null";
const token = localStorage.getItem("token");

/**
 * Initialize the dashboard on DOM load.
 */
document.addEventListener("DOMContentLoaded", () => {
    // Call renderContent if available (global from render.js)
    if (typeof renderContent === "function") {
        renderContent();
    }

    // Attach search bar listener
    const searchBar = document.getElementById("searchBar");
    if (searchBar) {
        searchBar.addEventListener("input", (e) => {
            const val = e.target.value.trim();
            patientName = val === "" ? "null" : val;
            loadAppointments();
        });
    }

    // Attach today button listener
    const todayButton = document.getElementById("todayButton");
    const datePicker = document.getElementById("datePicker");
    
    if (todayButton) {
        todayButton.addEventListener("click", () => {
            selectedDate = new Date().toISOString().split('T')[0];
            if (datePicker) datePicker.value = selectedDate;
            loadAppointments();
        });
    }

    // Attach date picker listener
    if (datePicker) {
        datePicker.value = selectedDate;
        datePicker.addEventListener("change", (e) => {
            selectedDate = e.target.value;
            loadAppointments();
        });
    }

    // Initial load
    loadAppointments();
});

/**
 * Fetches and displays appointments based on selected date and optional patient name.
 */
async function loadAppointments() {
    const tableBody = document.getElementById("patientTableBody");
    if (!tableBody) return;

    try {
        // Fetch appointments using the service layer
        const appointments = await getAllAppointments(selectedDate, patientName, token);
        
        // Clear existing content in the table
        tableBody.innerHTML = "";

        if (!appointments || appointments.length === 0) {
            // No appointments found fallback
            const noRow = document.createElement("tr");
            noRow.innerHTML = `<td colspan="5" style="text-align: center; color: gray; font-style: italic;">No Appointments found for today.</td>`;
            tableBody.appendChild(noRow);
            return;
        }

        // Loop through each appointment and construct a patient object
        appointments.forEach(appointment => {
            const patient = {
                id: appointment.patientId,
                name: appointment.patientName,
                phone: appointment.patientPhone,
                email: appointment.patientEmail
            };

            // Use createPatientRow to generate the <tr>
            const row = createPatientRow(patient, appointment);
            tableBody.appendChild(row);
        });

    } catch (error) {
        console.error("Error loading appointments:", error);
        // Fallback error message row
        tableBody.innerHTML = `<tr><td colspan="5" style="text-align: center; color: red;">Error loading appointments. Try again later.</td></tr>`;
    }
}
