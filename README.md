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
Appointment Management
Appointment scheduling & status updates

**Exception Handling**
Custom exceptions for handling errors

4. Modules Overview

4.1 Security Configuration (SecurityConfig.java)

Configures Spring Security to handle authentication and authorization.
Uses BCryptPasswordEncoder for password encoding.
Defines custom success handler for role-based redirection.

4.2 Controllers

**UserController**
Handles user registration (/signup) and login (/login).
Redirects authenticated users to respective dashboards based on roles.

**PatientController**
POST /patients/save-details → Save patient details
GET /patients/view-details → View patient details

**DoctorController**
POST /doctors/save-details → Save doctor details
GET /doctors/view-details → View doctor details
GET /doctors/update-availability → View availability update page
POST /doctors/save-availability → Update available slots

**AppointmentController**
GET /appointments/book → View appointment booking page
POST /appointments/book → Book an appointment
GET /appointments/view → View booked appointments
POST /appointments/cancel → Cancel an appointment

**MedicationController**
GET /medications/add → View medication form
POST /medications/add → Add medication prescription
GET /medications/view → View medications
POST /medications/delete → Delete medication

4.3 Service Layer

UserService: Handles user authentication and registration.
PatientService: Manages patient details.
DoctorService: Handles doctor data and availability slots.
AppointmentService: Manages booking, retrieval, and cancellation of appointments.
Availability service: Handlesavailability slots of doctors.
MedicationService: Handles medication prescriptions.

4.4 Repository Layer

UserRepository: Interface for user-related queries.
PatientRepository: Manages patient entity interactions.
DoctorRepository: Retrieves doctor data based on user ID.
AppointmentRepository: Handles appointment scheduling and retrieval.
MedicationRepository: Manages prescribed medications.
AvailabilityRepository: Stores doctor availability slots.

4.5 Entities (Database Models)

User: Stores user information (email, name, password, role).
Patient: Stores patient details (age, gender, medical history, user reference).
Doctor: Stores doctor specialization, contact, and user reference.
Appointment: Stores appointment details (doctor, patient, time, status).
Availability: Stores available time slots for doctors.
Medication: Stores prescribed medications for appointments.

4.6 Exception Handling

GlobalExceptionHandler: Centralized exception handling for application errors.

**Custom Exceptions:**
PatientNotFoundException
DoctorNotFoundException
AppointmentNotFoundException
MedicationNotFoundException

5. Templates (Thymeleaf Views)

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

6. How to Run the Project

Configure database connection in application.properties.
Run the application
Access the application at http://localhost:8080
