/**
 * header.js - Dynamically renders the header based on user role and session status.
 */

/**
 * Renders the header content into the #header div.
 */
function renderHeader() {
    const headerDiv = document.getElementById("header");
    if (!headerDiv) return;

    // 1. Check if the current page is the homepage
    if (window.location.pathname === "/" || window.location.pathname.endsWith("/index.html")) {
        localStorage.removeItem("userRole");
        localStorage.removeItem("token");
        headerDiv.innerHTML = `
            <header class="header">
                <div class="logo-section">
                    <img src="../assets/images/logo/logo.png" alt="Hospital CRM Logo" class="logo-img">
                    <span class="logo-title">Hospital CMS</span>
                </div>
            </header>`;
        return;
    }

    // 2. Retrieve user role and token from localStorage
    const role = localStorage.getItem("userRole");
    const token = localStorage.getItem("token");

    // 3. Handle session expiry or invalid login
    if ((role === "loggedPatient" || role === "admin" || role === "doctor") && !token) {
        localStorage.removeItem("userRole");
        localStorage.removeItem("token");
        alert("Session expired or invalid login. Please log in again.");
        window.location.href = "/";
        return;
    }

    // 4. Initialize Header Content
    let headerContent = `
        <header class="header">
            <div class="logo-section" onclick="window.location.href='/'" style="cursor: pointer;">
                <img src="../assets/images/logo/logo.png" alt="Hospital CRM Logo" class="logo-img">
                <span class="logo-title">Hospital CMS</span>
            </div>
            <nav class="nav-links">`;

    // 5. Add Role-Specific Content
    if (role === "admin") {
        headerContent += `
            <button id="addDocBtn" class="adminBtn" onclick="openModal('addDoctor')">Add Doctor</button>
            <a href="#" class="logout-link" onclick="logout()">Logout</a>`;
    } else if (role === "doctor") {
        headerContent += `
            <button class="adminBtn" onclick="window.location.href='/pages/doctorDashboard.html'">Home</button>
            <a href="#" class="logout-link" onclick="logout()">Logout</a>`;
    } else if (role === "patient") {
        headerContent += `
            <button id="patientLogin" class="adminBtn">Login</button>
            <button id="patientSignup" class="adminBtn">Sign Up</button>`;
    } else if (role === "loggedPatient") {
        headerContent += `
            <button id="home" class="adminBtn" onclick="window.location.href='/pages/patientDashboard.html'">Home</button>
            <button id="patientAppointments" class="adminBtn" onclick="window.location.href='/pages/patientAppointments.html'">Appointments</button>
            <a href="#" class="logout-link" onclick="logoutPatient()">Logout</a>`;
    }

    headerContent += `
            </nav>
        </header>`;

    // 6. Inject Header and Attach Listeners
    headerDiv.innerHTML = headerContent;
    attachHeaderButtonListeners();
}

/**
 * Attaches event listeners to dynamically created header buttons.
 */
function attachHeaderButtonListeners() {
    const loginBtn = document.getElementById("patientLogin");
    const signupBtn = document.getElementById("patientSignup");

    if (loginBtn) {
        loginBtn.addEventListener("click", () => {
            if (typeof openModal === "function") openModal("patientLogin");
        });
    }

    if (signupBtn) {
        signupBtn.addEventListener("click", () => {
            if (typeof openModal === "function") openModal("patientSignup");
        });
    }
}

/**
 * Clears the session and redirects to the homepage.
 */
function logout() {
    localStorage.removeItem("userRole");
    localStorage.removeItem("token");
    window.location.href = "/";
}

/**
 * Logs out a patient but retains the "patient" role for guest access.
 */
function logoutPatient() {
    localStorage.removeItem("token");
    localStorage.setItem("userRole", "patient");
    window.location.href = "/pages/patientDashboard.html";
}

// Automatically render the header on script load
document.addEventListener("DOMContentLoaded", renderHeader);
