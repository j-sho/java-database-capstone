# Schema Design

## MySQL Database Design

The following tables represent the core relational data of the Smart Clinic Management System. MySQL is used for these entities due to the need for structured relationships and ACID compliance.

### Table: patients
- `id`: INT, Primary Key, Auto Increment
- `name`: VARCHAR(100), Not Null
- `email`: VARCHAR(100), Unique, Not Null
- `password`: VARCHAR(255), Not Null
- `phone`: VARCHAR(15), Not Null
- `address`: TEXT
- **Notes**: Phone validation (10 digits) and email format validation are enforced at the application layer.

### Table: doctors
- `id`: INT, Primary Key, Auto Increment
- `name`: VARCHAR(100), Not Null
- `specialization`: VARCHAR(100), Not Null
- `email`: VARCHAR(100), Unique, Not Null
- `phone`: VARCHAR(15), Not Null

### Table: appointments
- `id`: INT, Primary Key, Auto Increment
- `doctor_id`: INT, Foreign Key → `doctors(id)`
- `patient_id`: INT, Foreign Key → `patients(id)`
- `appointment_time`: DATETIME, Not Null
- `status`: INT (0 = Scheduled, 1 = Completed, 2 = Cancelled)
- **Notes**: If a patient is deleted, appointments should ideally be archived or deleted (ON DELETE CASCADE) to maintain data integrity. Overlapping appointments for a doctor are prevented via business logic in the service layer.

### Table: admin
- `id`: INT, Primary Key, Auto Increment
- `username`: VARCHAR(50), Unique, Not Null
- `password`: VARCHAR(255), Not Null

---

## MongoDB Collection Design

MongoDB is used for prescriptions to allow for a flexible schema that can accommodate varying medication details, doctor notes, and pharmacy metadata without requiring complex table joins or rigid column structures.

### Collection: prescriptions
```json
{
  "_id": "ObjectId('64abc123456')",
  "patientName": "John Smith",
  "patientId": 101,
  "appointmentId": 51,
  "medication": "Paracetamol",
  "dosage": "500mg",
  "doctorNotes": "Take 1 tablet every 6 hours if fever persists.",
  "dateIssued": "2026-05-03T10:00:00Z",
  "refillCount": 2,
  "pharmacy": {
    "name": "Central Pharma",
    "location": "123 Healthcare Ave"
  }
}
```

**Design Justification:**
- **Patient ID Reference**: We store the `patientId` and `appointmentId` to maintain a link to the MySQL relational data.
- **Embedded Objects**: The `pharmacy` details are embedded as a sub-document since pharmacy information is specific to the prescription context and doesn't require a separate collection in this use case.
- **Flexibility**: If we need to add new fields like "lab results" or "allergy warnings" in the future, MongoDB allows us to do so without migrating a fixed schema.
