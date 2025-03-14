# MedicalManagement
1. Introduction

This project is a Doctor-Patient Appointment Management System, built using Spring Boot, Thymeleaf, Spring Security, and JPA. The system allows patients to register and book appointments with doctors, while doctors can manage their availability and prescribe medications.
Doctor-Patient Appointment Management Application - Documentation

2. System Architecture

Technologies Used:
Backend: Spring Boot, Spring Security, Spring Data JPA
Frontend: Thymeleaf, HTML, CSS, Bootstrap
Database: MySQL
Security: Spring Security with BCrypt password encoding

3. Features

**User Authentication & Role Management**

Users can sign up as Doctors or Patients.
Login authentication is handled using Spring Security.
BCrypt password encoding is used for enhanced security.
Role-based access control is enforced to restrict unauthorized access.

**Doctor Features**

Register & log in
Profile completion
Manage availability slots
View appointments
Prescribe medications
Delete medications

**Patient Features**

Register & log in
Book & view appointments
Cancel appointments
View prescribed medications

**Appointment Management**

Patients can book appointments with doctors based on available slots.
Doctors can confirm, cancel, or reschedule appointments.
The system maintains an audit trail for all appointment-related actions.

**Medication Management**

Doctors can prescribe medications for each appointment.
Patients can view their prescriptions through their dashboard.

**Exception Handling**

Custom exceptions are implemented to handle errors gracefully.
The system provides user-friendly error messages for invalid actions.

4. Security Configuration (SecurityConfig.java)

Configures Spring Security to handle authentication and authorization.
Uses BCryptPasswordEncoder for password encoding.
Defines custom success handler for role-based redirection.
Implements CSRF protection and session management.

5. Controllers

**UserController**

Handles user registration (/signup) and login (/login).
Redirects authenticated users based on roles.
Implements email validation to prevent duplicate registrations.

**PatientController**

POST /patients/save-details → Save patient details
GET /patients/view-details → Fetches patient details for their profile

**DoctorController**

POST /doctors/save-details → Displays available doctors and slots for appointment booking.
GET /doctors/view-details → View doctor details
GET /doctors/update-availability → Displays available slots management page.
POST /doctors/save-availability → Update available slots

**AppointmentController**

GET /appointments/book → View appointment booking page
POST /appointments/book → Books an appointment and updates availability.
GET /appointments/view → View booked appointments
POST /appointments/cancel → Cancels an appointment and restores the slot.

**MedicationController**

GET /medications/add → View medication form
POST /medications/add → Add medication prescription
GET /medications/view → View medications
POST /medications/delete → Delete medication

6. Service Layer

UserService: Handles user authentication, registration, and role management.
PatientService: Manages patient details and retrieves medical history.
DoctorService: Handles doctor profile updates and availability slots.
AppointmentService: Implements appointment booking, cancellation, and status updates.
MedicationService: Handles prescription management for doctors and patients.

7. Repository Layer

UserRepository: Fetches and updates user records.
PatientRepository: Manages patient data.
DoctorRepository: Retrieves and updates doctor records.
AppointmentRepository: Stores and retrieves appointment details.
MedicationRepository: Handles medication storage and retrieval.
AvailabilityRepository: Manages available time slots for doctors.

8. Entities (Database Models)

User: Stores email, name, encrypted password, and role.
Patient: Contains age, gender, medical history, and references a user.
Doctor: Stores specialization, contact details, and references a user.
Appointment: Manages doctor-patient appointments with timestamps.
Availability: Stores available time slots for doctors.
Medication: Stores prescribed medicines for patient appointments.

9. Exception Handling

GlobalExceptionHandler: Centralized exception handling for application errors.

**Custom Exceptions:**
PatientNotFoundException
DoctorNotFoundException
AppointmentNotFoundException
MedicationNotFoundException

10. Templates (Thymeleaf Views)

login.html → Login page
signup.html → User registration page
doctor-page.html → Doctor's dashboard
patient-page.html → Patient's dashboard
appointment-booking.html → Appointment scheduling
medication-form.html → Prescription entry
appointments-view.html
availability-slots.html
doctor-appointments.html
doctor-details.html
medications-view.html
patient-details.html
view-medication.html 

11. How to Run the Project

Configure database connection in application.properties.
Add the required dependencies in POM.xml
Run the application
Access the application at http://localhost:8080
