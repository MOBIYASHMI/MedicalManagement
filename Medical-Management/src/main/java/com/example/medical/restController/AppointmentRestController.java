package com.example.medical.restController;

import com.example.medical.dto.AppointmentDto;
import com.example.medical.dto.PatientDto;
import com.example.medical.dto.UserDto;
import com.example.medical.entity.Availability;
import com.example.medical.entity.Doctor;
import com.example.medical.entity.Patient;
import com.example.medical.entity.User;
import com.example.medical.exceptions.AppointmentNotFoundException;
import com.example.medical.exceptions.DoctorNotFoundException;
import com.example.medical.exceptions.PatientNotFoundException;
import com.example.medical.repository.AvailabilityRepository;
import com.example.medical.repository.DoctorRepository;
import com.example.medical.repository.PatientRepository;
import com.example.medical.repository.UserRepository;
import com.example.medical.service.AppointmentService;
import com.example.medical.service.PatientService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentRestController {
    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    PatientService patientService;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    AvailabilityRepository availabilityRepository;


    @PostMapping("/book")
    public ResponseEntity<?> bookAppointment(@Valid @RequestBody AppointmentDto appointmentDto, BindingResult result) {
        if (appointmentDto.getStatus() == null || appointmentDto.getStatus().trim().isEmpty()) {
            appointmentDto.setStatus("BOOKED");
        }
        if (result.hasErrors()) {
            Map<String, String> errors=new HashMap<>();
            result.getFieldErrors().forEach(error -> errors.put(error.getField(),error.getDefaultMessage()));
            return ResponseEntity.badRequest().body(errors);
        }

        LocalDateTime selectedTime=appointmentDto.getAppointmentTime();

        boolean isSlotAvailable=availabilityRepository.existsByDoctorIdAndAvailableSlot(appointmentDto.getDoctorId(),selectedTime);
        if(!isSlotAvailable){
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", "The selected slot is no longer available. Please choose another."));
        }
        appointmentService.saveAppointment(appointmentDto);

        appointmentService.deleteAvailability(appointmentDto.getDoctorId(),selectedTime);
        return ResponseEntity.ok().body("Appointment Booked successfully");
    }

    @GetMapping("/view")
    public ResponseEntity<?> viewAppointments(@RequestParam(required = false) Long doctorId, @RequestParam(required = false) Long patientId, Principal principal) {
        try {
            List<AppointmentDto> appointments;

            if (doctorId != null) {
                appointments=appointmentService.getAppointmentsByDoctor(doctorId);
            } else if (patientId != null) {
                appointments=appointmentService.getAppointmentsByPatient(patientId);
            }else {
                // If no doctor or patient ID is provided, fetch based on logged-in user
                User user = userRepository.findByEmail(principal.getName())
                        .orElseThrow(() -> new UsernameNotFoundException("User not found"));

                if (user.getRole().equals("DOCTOR")) {
                    Doctor doctor = doctorRepository.findByUserId(user.getId())
                            .orElseThrow(() -> new DoctorNotFoundException("Doctor not found"));
                    appointments=appointmentService.getAppointmentsByDoctor(doctor.getId());
                    return ResponseEntity.ok().body(appointments);
                } else if(user.getRole().equals("PATIENT")){
                    Patient patient = patientRepository.findByUserId(user.getId())
                            .orElseThrow(() -> new PatientNotFoundException("Patient not found"));
                    appointments=appointmentService.getAppointmentsByPatient(patient.getId());
                    return ResponseEntity.ok().body(appointments);
                }else {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN)
                            .body(Collections.singletonMap("error", "Invalid role for user"));
                }
            }
            return ResponseEntity.ok(appointments);
        } catch (DoctorNotFoundException | PatientNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonMap("error", ex.getMessage()));
        }
    }

    @PostMapping("/cancel")
    public ResponseEntity<?> cancelAppointment(@RequestParam Long appointmentId) {
        try {
            AppointmentDto appointmentDto = appointmentService.getAppointmentDetails(appointmentId);

            if (appointmentDto == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Collections.singletonMap("error", "Appointment not found"));
            }

            appointmentService.cancelAppointment(appointmentId);

            Doctor doctor = doctorRepository.findById(appointmentDto.getDoctorId())
                    .orElseThrow(() -> new RuntimeException("Doctor not found"));

            Availability restoredSlot = new Availability(appointmentDto.getAppointmentTime(), doctor);
            availabilityRepository.save(restoredSlot);

            return ResponseEntity.ok(Collections.singletonMap("message", "Appointment cancelled successfully"));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "An error occurred while canceling the appointment"));
        }
    }
}
