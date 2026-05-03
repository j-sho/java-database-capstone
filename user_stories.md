# User Story Template

**Title:**
_As a [user role], I want [feature/goal], so that [reason]._

**Acceptance Criteria:**
1. [Criteria 1]
2. [Criteria 2]
3. [Criteria 3]

**Priority:** [High/Medium/Low]
**Story Points:** [Estimated Effort in Points]
**Notes:**
- [Additional information or edge cases]

---

**Title:**
_As an admin, I want to log into the portal with my username and password, so that I can manage the platform securely._

**Acceptance Criteria:**
1. Admin enters valid username and password.
2. System validates credentials against the MySQL database.
3. Admin is redirected to the Admin Dashboard upon successful login.

**Priority:** High
**Story Points:** 3
**Notes:**
- Password should be securely hashed in the database.

---

**Title:**
_As an admin, I want to log out of the portal, so that I can protect system access from unauthorized users._

**Acceptance Criteria:**
1. Admin clicks the logout button.
2. Session or token is invalidated on the server side.
3. Admin is redirected back to the login page and cannot access protected routes.

**Priority:** High
**Story Points:** 1
**Notes:**
- Ensure the JWT or session token is properly cleared from the client.

---

**Title:**
_As an admin, I want to add doctors to the portal, so that they can manage their schedules and appointments._

**Acceptance Criteria:**
1. Admin enters doctor details including name, specialization, and contact information.
2. System saves the doctor profile in the MySQL database.
3. A success message is displayed and the new doctor appears in the dashboard list.

**Priority:** High
**Story Points:** 5
**Notes:**
- Email validation and uniqueness checks should be performed.

---

**Title:**
_As an admin, I want to delete a doctor's profile from the portal, so that inactive or incorrect profiles are removed._

**Acceptance Criteria:**
1. Admin selects a doctor from the management list to delete.
2. System prompts for confirmation before final deletion.
3. Doctor profile is removed from the MySQL database and the UI is updated.

**Priority:** Medium
**Story Points:** 3
**Notes:**
- Implement cascading rules or checks for associated appointments before deletion.

---

**Title:**
_As an admin, I want to run a stored procedure in MySQL CLI to get the number of appointments per month, so that I can track usage statistics._

**Acceptance Criteria:**
1. Admin executes the stored procedure in the MySQL CLI environment.
2. The procedure returns an accurate count of appointments grouped by month.
3. The output is clearly formatted for statistical analysis.

**Priority:** Medium
**Story Points:** 5
**Notes:**
- Requires the stored procedure to be pre-defined in the MySQL database schema.

---

**Title:**
_As a patient, I want to view a list of doctors without logging in, so that I can explore options before registering._

**Acceptance Criteria:**
1. Patient accesses the public doctor listing page.
2. System displays a list of available doctors and their specializations.
3. No login is required to view this information.

**Priority:** Medium
**Story Points:** 3
**Notes:**
- Only public profile information should be visible to unauthenticated users.

---

**Title:**
_As a patient, I want to sign up using my email and password, so that I can book appointments._

**Acceptance Criteria:**
1. Patient enters name, email, and password in the registration form.
2. System validates input and saves the record in the MySQL database.
3. Patient receives a confirmation message and can immediately log in.

**Priority:** High
**Story Points:** 5
**Notes:**
- Implement email format validation and password strength checks.

---

**Title:**
_As a patient, I want to log into the portal, so that I can manage my bookings._

**Acceptance Criteria:**
1. Patient enters registered email and password.
2. System validates credentials and grants access.
3. Patient is redirected to their personal dashboard.

**Priority:** High
**Story Points:** 3
**Notes:**
- Secure authentication mechanism (e.g., JWT) should be used.

---

**Title:**
_As a patient, I want to log out of the portal, so that I can secure my account._

**Acceptance Criteria:**
1. Patient clicks the logout link/button.
2. Active session is terminated and local tokens are cleared.
3. Patient is redirected to the landing page.

**Priority:** High
**Story Points:** 1
**Notes:**
- Standard security feature to prevent unauthorized access on shared devices.

---

**Title:**
_As a patient, I want to book an hour-long appointment to consult with a doctor, so that I can receive medical advice._

**Acceptance Criteria:**
1. Logged-in patient selects a doctor and an available time slot.
2. System creates an appointment record for a 60-minute duration in the MySQL database.
3. A confirmation message with appointment details is displayed.

**Priority:** High
**Story Points:** 5
**Notes:**
- Ensure the time slot is marked as unavailable once booked.

---

**Title:**
_As a patient, I want to view my upcoming appointments, so that I can prepare accordingly._

**Acceptance Criteria:**
1. Patient navigates to the "My Appointments" section in the dashboard.
2. System retrieves and displays a list of future scheduled appointments.
3. Appointments are clearly presented with doctor name, date, and time.

**Priority:** Medium
**Story Points:** 3
**Notes:**
- Data is fetched from the MySQL appointment table.

---

**Title:**
_As a doctor, I want to log into the portal, so that I can manage my appointments efficiently._

**Acceptance Criteria:**
1. Doctor enters registered credentials on the login page.
2. System validates the doctor role and credentials against the MySQL database.
3. Doctor is successfully redirected to the specialized Doctor Dashboard.

**Priority:** High
**Story Points:** 3
**Notes:**
- Ensure role-based access control is strictly enforced.

---

**Title:**
_As a doctor, I want to log out of the portal, so that I can protect my professional and patient data._

**Acceptance Criteria:**
1. Doctor clicks the logout button in the dashboard navigation.
2. Active session is terminated and authentication tokens are invalidated.
3. Doctor is redirected to the public login screen.

**Priority:** High
**Story Points:** 1
**Notes:**
- Essential for maintaining data privacy and security standards.

---

**Title:**
_As a doctor, I want to view my appointment calendar, so that I can stay organized and manage my daily schedule._

**Acceptance Criteria:**
1. Doctor navigates to the "My Schedule" section of the portal.
2. System retrieves all scheduled appointments from the MySQL database.
3. Appointments are displayed in a clear, chronological calendar or list format.

**Priority:** High
**Story Points:** 5
**Notes:**
- The view should include patient names and precise appointment times.

---

**Title:**
_As a doctor, I want to mark my unavailability, so that patients only see and book available slots._

**Acceptance Criteria:**
1. Doctor selects specific time blocks or days to mark as unavailable.
2. System updates the availability record in the MySQL database.
3. The booking interface for patients automatically hides or disables these slots.

**Priority:** Medium
**Story Points:** 5
**Notes:**
- Prevents double-booking and manual scheduling conflicts.

---

**Title:**
_As a doctor, I want to update my profile with specialization and contact information, so that patients have up-to-date information._

**Acceptance Criteria:**
1. Doctor accesses the "Edit Profile" section of their dashboard.
2. Doctor updates fields such as specialization, bio, and contact details.
3. System persists changes to the MySQL database and updates the public-facing profile.

**Priority:** Medium
**Story Points:** 3
**Notes:**
- Changes should be reflected instantly on the patient search pages.

---

**Title:**
_As a doctor, I want to view the patient details for upcoming appointments, so that I can be prepared for each consultation._

**Acceptance Criteria:**
1. Doctor selects an upcoming appointment from their dashboard list.
2. System retrieves and displays relevant patient information (name, contact, reason for visit).
3. Doctor can review this information prior to the start of the appointment.

**Priority:** Medium
**Story Points:** 3
**Notes:**
- Access to patient details must be restricted to the assigned doctor only.
