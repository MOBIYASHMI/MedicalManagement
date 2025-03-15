# MedicalManagement
**1. Introduction**

This project is a Doctor-Patient Appointment Management System, built using Spring Boot, Thymeleaf, Spring Security, and JPA. The system allows patients to register and book appointments with doctors, while doctors can manage their availability and prescribe medications.
Doctor-Patient Appointment Management Application - Documentation

**2. System Architecture**

Technologies Used:
Backend: Spring Boot, Spring Security, Spring Data JPA
Frontend: Thymeleaf, HTML, CSS, Bootstrap
Database: MySQL
Security: Spring Security with BCrypt password encoding

**3. Features**

**User Authentication & Role Management**

* Users can sign up as Doctors or Patients.

* Login authentication is handled using Spring Security.

* BCrypt password encoding is used for enhanced security.

* Role-based access control is enforced to restrict unauthorized access.

**Doctor Features**

* Register & log in

* Profile completion

* Manage availability slots

* View appointments

* Prescribe medications

* Delete medications

**Patient Features**

* Register & log in

* Book & view appointments

* Cancel appointments

* View prescribed medications

**Appointment Management**

* Patients can book appointments with doctors based on available slots.

* Doctors can confirm, cancel, or reschedule appointments.

* The system maintains an audit trail for all appointment-related actions.

**Medication Management**

* Doctors can prescribe medications for each appointment.

* Patients can view their prescriptions through their dashboard.

**Exception Handling**

* Custom exceptions are implemented to handle errors gracefully.

* The system provides user-friendly error messages for invalid actions.

**4. Security Configuration (SecurityConfig.java)**

* Configures Spring Security to handle authentication and authorization.

* Uses BCryptPasswordEncoder for password encoding.

* Defines custom success handler for role-based redirection.

* Implements CSRF protection and session management.

5. Controllers

**UserController**

* Handles user registration (/signup) and login (/login).

* Redirects authenticated users based on roles.

* Implements email validation to prevent duplicate registrations.

**PatientController**

* POST /patients/save-details → Save patient details

* GET /patients/view-details → Fetches patient details for their profile

**DoctorController**

* POST /doctors/save-details → Displays available doctors and slots for appointment booking.

* GET /doctors/view-details → View doctor details

* GET /doctors/update-availability → Displays available slots management page.

* POST /doctors/save-availability → Update available slots

**AppointmentController**

* GET /appointments/book → View appointment booking page

* POST /appointments/book → Books an appointment and updates availability.

* GET /appointments/view → View booked appointments

* POST /appointments/cancel → Cancels an appointment and restores the slot.

**MedicationController**

* GET /medications/add → View medication form

* POST /medications/add → Add medication prescription

* GET /medications/view → View medications

* POST /medications/delete → Delete medication

**6. Service Layer**

* UserService: Handles user authentication, registration, and role management.

* PatientService: Manages patient details and retrieves medical history.

* DoctorService: Handles doctor profile updates and availability slots.

* AppointmentService: Implements appointment booking, cancellation, and status updates.

* MedicationService: Handles prescription management for doctors and patients.

**7. Repository Layer**

* UserRepository: Fetches and updates user records.

* PatientRepository: Manages patient data.

* DoctorRepository: Retrieves and updates doctor records.

* AppointmentRepository: Stores and retrieves appointment details.

* MedicationRepository: Handles medication storage and retrieval.

* AvailabilityRepository: Manages available time slots for doctors.

**8. Entities (Database Models)**

* User: Stores email, name, encrypted password, and role.

* Patient: Contains age, gender, medical history, and references a user.

* Doctor: Stores specialization, contact details, and references a user.

* Appointment: Manages doctor-patient appointments with timestamps.

* Availability: Stores available time slots for doctors.

* Medication: Stores prescribed medicines for patient appointments.

**9. Exception Handling**

GlobalExceptionHandler: Centralized exception handling for application errors.

**Custom Exceptions:**

* PatientNotFoundException

* DoctorNotFoundException

* AppointmentNotFoundException

* MedicationNotFoundException

**10. Templates (Thymeleaf Views)**

* login.html → Login page

* signup.html → User registration page

* doctor-page.html → Doctor's dashboard

* patient-page.html → Patient's dashboard

* appointment-booking.html → Appointment scheduling

* medication-form.html → Prescription entry

* appointments-view.html

* availability-slots.html

* doctor-appointments.html

* doctor-details.html

* medications-view.html

* patient-details.html

* view-medication.html 

**11. How to Run the Project**

* Configure database connection in application.properties.

* Add the required dependencies in POM.xml

* Run the application

* Access the application at http://localhost:8080

**Testing Overview**

**1. Testing Frameworks & Tools**

**JUnit 5:** For writing unit tests.
JUnit 5 Annotations

* @Test - Marks a method as a test case.

* @BeforeEach - Executes setup logic before each test method.

* @ExtendWith(MockitoExtension.class) - Enables Mockito support.

* @SpringBootTest - Loads the Spring application context for integration testing.

**Mockito:** For mocking dependencies in service and repository tests.
Mockito annotations

* @Mock - Creates a mock instance of a class.

* @InjectMocks - Injects mock dependencies into a class under test.

* @MockitoBean - A custom annotation used to mock Spring beans in tests.

**Spring Boot Test:** For integration testing.
Spring boot test annotations

* @WebMvcTest(UserController.class) - Loads only the controller layer for testing.

* @WithMockUser - Simulates a logged-in user in security tests.

**MockMvc:** For testing controller endpoints.

**AssertJ & Hamcrest:** For assertions and validations.

**2. Controller Tests**

Controller tests ensure that HTTP requests are processed correctly and return the expected responses.

**Annotations Used:**

* @WebMvcTest – Used to test Spring MVC controllers.

* @ExtendWith(MockitoExtension.class) – Enables Mockito support.

* @MockitoBean – Mocks dependencies for testing.

* @Autowired – Injects dependencies into test classes.

* @BeforeEach – Runs setup before each test.

* @Test – Marks methods as test cases.

**UserControllerTest**

* Tests the signup, login, and validation functionalities.

* Ensures users can register successfully and redirects to login.

* Validates form inputs like email format and password length.

* Tests if the login page loads correctly.

* Checks if doctors and patients can access their respective pages.

**PatientControllerTest**

* Tests patient registration and information retrieval.

* Ensures patients can save their details and get redirected.

* Validates missing or incorrect fields in the patient form.

* Tests handling of non-existing users or missing patient records.

* Ensures the correct patient details are displayed when requested.

**DoctorControllerTest**

* Tests doctor registration and availability management.

* Ensures doctors can save their profile details and view them.

* Validates specialization, contact number, and other fields.

* Tests if doctors can update their availability slots.

* Handles cases where a doctor record is missing in the system.

**AppointmentControllerTest**

* Tests booking, viewing, and canceling appointments.

* Ensures patients can book available slots.

* Validates appointment details and handles cases where slots are unavailable.

* Ensures both doctors and patients can view their appointments.

* Tests if appointments can be canceled and availability is restored.

**AvailabilityControllerTest**

* Tests managing doctor availability slots.

* Ensures doctors can add availability slots for patients to book.

* Handles validation errors for missing or incorrect slot entries.

* Verifies that available slots are correctly retrieved and displayed.

**MedicationControllerTest**

* Tests managing patient prescriptions.

* Ensures doctors can add medications to a patient’s appointment.

* Verifies that medications are correctly retrieved and displayed.

* Handles cases where medications need to be updated or deleted.

**3. Service Layer Tests**

Service tests verify business logic implementation and interaction with repositories.

**Annotations Used:**

* @SpringBootTest – Loads the full Spring Boot application context.

* @MockBean – Mocks service dependencies.

* @InjectMocks – Injects dependencies into test classes.

**UserServiceTest**

* Tests user management functions like registration and retrieval.

* Ensures users can register successfully, with passwords being properly encoded.

* Checks if a user can be retrieved by email.

* Validates that an exception is thrown if the user is not found.

**PatientServiceTest**

* Tests patient-related operations like saving and retrieving patient details.

* Ensures patient details are saved correctly, even if they already exist.

* Handles cases where the user does not exist before saving details.

* Checks if patient details can be retrieved properly.

* Validates the scenario where a patient record is missing.

**DoctorServiceTest**

* Tests doctor-related operations like registration and availability updates.

* Ensures doctors can save their details, including specialization and contact info.

* Handles cases where a doctor record does not exist.

* Tests the retrieval of doctor details by their user ID.

* Checks if doctors can update their availability slots.

* Ensures availability slots are properly retrieved and displayed.

**AppointmentServiceTest**

* Tests appointment booking, retrieval, and cancellation.

* Ensures appointments are booked correctly when slots are available.

* Validates that an exception is thrown if a doctor or patient is missing.

* Tests appointment retrieval for both doctors and patients.

* Ensures appointments can be canceled and their slots freed up.

**AvailabilityServiceTest**

* Tests doctor availability slot management.

* Ensures doctors can add and update their available slots.

* Checks if available slots can be fetched correctly for a doctor.

* Handles cases where availability does not exist.

* Ensures slots are properly deleted after being booked.

**MedicationServiceTest**

* Tests medication management for patient prescriptions.

* Ensures doctors can add or update medications for an appointment.

* Checks if medications can be retrieved correctly for a given appointment.

* Handles cases where no medication is found for an appointment.

* Ensures medications can be deleted when necessary.

**4. Repository Layer Tests**

Repository tests validate database interactions.

**Annotations Used:**

* @ExtendWith(MockitoExtension.class) – Enables Mockito framework.

* @Mock – Mocks repository interfaces.

* @BeforeEach – Runs setup before each test.

* @Test – Marks methods as test cases.

**UserRepositoryTest**

* Tests user-related database operations.

* Ensures users can be retrieved by email.

* Checks if the repository correctly returns an empty result for non-existing users.

* Verifies that user data is saved successfully.

**PatientRepositoryTest**

* Tests database interactions for patient records.

* Ensures patients can be retrieved using their user ID.

* Validates that empty results are returned when no patient exists.

* Verifies that patient records are saved correctly.

**DoctorRepositoryTest**

* Tests doctor-related database queries.

* Ensures doctors can be retrieved by their user ID.

* Checks if the repository correctly returns an empty result for non-existing doctors.

* Verifies that doctor data is saved and updated properly.

**AppointmentRepositoryTest**

* Tests appointment-related database operations.

* Ensures appointments can be retrieved by patient or doctor.

* Validates that appointments are saved correctly.

* Checks if canceled appointments are properly removed from the repository.

**AvailabilityRepositoryTest**

* Tests doctor availability storage and retrieval.

* Ensures availability slots are retrieved correctly for a doctor.

* Checks if a specific availability slot exists for a doctor.

* Verifies that availability slots can be deleted when necessary.

**MedicationRepositoryTest**

* Tests medication storage and retrieval.

* Ensures medications are retrieved by appointment ID.

* Validates that the repository correctly returns an empty result for missing medications.

* Verifies that medications are saved and deleted properly.

**Mycontroller**
Swagger to document the endpoints and check the functionality of API's.
