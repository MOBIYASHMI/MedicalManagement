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

**Testing Overview**

1. Testing Frameworks & Tools

**JUnit 5:** For writing unit tests.
JUnit 5 Annotations
@Test - Marks a method as a test case.
@BeforeEach - Executes setup logic before each test method.
@ExtendWith(MockitoExtension.class) - Enables Mockito support.
@SpringBootTest - Loads the Spring application context for integration testing.
Mockito: For mocking dependencies in service and repository tests.
Spring Boot Test: For integration testing.
MockMvc: For testing controller endpoints.
AssertJ & Hamcrest: For assertions and validations.

2. Controller Tests

Controller tests ensure that HTTP requests are processed correctly and return the expected responses.

Annotations Used:
@WebMvcTest – Used to test Spring MVC controllers.
@ExtendWith(MockitoExtension.class) – Enables Mockito support.
@MockitoBean – Mocks dependencies for testing.
@Autowired – Injects dependencies into test classes.
@BeforeEach – Runs setup before each test.
@Test – Marks methods as test cases.

UserControllerTest

Test user registration page (/signup) → Checks if the page loads properly.
Test successful user registration → Ensures a new user can be saved successfully.
Test user registration with existing email → Checks if the system prevents duplicate registrations.
Test user login page (/login) → Verifies login page rendering.
Test role-based redirections → Ensures doctors and patients are redirected correctly upon login.

DoctorControllerTest

Test doctor registration (/doctors/save-details) → Validates doctor profile creation.
Test updating doctor availability → Ensures doctors can modify their available slots.
Test retrieving doctor details (/doctors/view-details) → Fetches doctor information.

4.3 Service Layer Tests

Service tests verify business logic implementation and interaction with repositories.

Annotations Used:

@SpringBootTest – Loads the full Spring Boot application context.

@MockBean – Mocks service dependencies.

@InjectMocks – Injects dependencies into test classes.

UserServiceTest

@Test
void testSaveUser() {
    when(passwordEncoder.encode(userDto.getPassword())).thenReturn("encodedPassword");
    when(userRepository.save(any(User.class))).thenReturn(user);

    User savedUser = userService.save(userDto);
    assertThat(savedUser).isNotNull();
    assertThat(savedUser.getEmail()).isEqualTo("test@gmail.com");
}

Test saving a new user → Checks password encoding and persistence.

Test retrieving users by email → Ensures email lookup functionality.

4.4 Repository Layer Tests

Repository tests validate database interactions.

Annotations Used:

@ExtendWith(MockitoExtension.class) – Enables Mockito framework.

@Mock – Mocks repository interfaces.

@BeforeEach – Runs setup before each test.

@Test – Marks methods as test cases.

UserRepositoryTest

@Test
void testFindByEmail_WhenUserExists() {
    when(userRepository.findByEmail("test@gmail.com")).thenReturn(Optional.of(user));
    Optional<User> foundUser = userRepository.findByEmail("test@gmail.com");
    assertThat(foundUser).isPresent();
    assertThat(foundUser.get().getEmail()).isEqualTo("test@gmail.com");
}

Test finding user by email → Ensures user retrieval by email.

Test saving users → Checks data persistence.
