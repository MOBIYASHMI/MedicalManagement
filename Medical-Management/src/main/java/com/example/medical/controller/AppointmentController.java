package com.example.medical.controller;

import com.example.medical.dto.AppointmentDto;
import com.example.medical.dto.PatientDto;
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
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;


@Controller
@RequestMapping("/appointments")
public class AppointmentController {

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PatientService patientService;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private AvailabilityRepository availabilityRepository;

    @GetMapping("/book")
    public String showBookingPage(Model model, Principal principal) {
        User user=userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        PatientDto patientdto = patientService.getPatientDetails(user.getId());

//        Long userId=patient.getUser().getId();
//        Patient finalPatient=patientRepository.findByUserId(userId)
//                .orElseThrow(() -> new PatientNotFoundException("Patient not found"));

        AppointmentDto appointmentDto=new AppointmentDto();
        appointmentDto.setPatientId(patientdto.getUserId());
        appointmentDto.setStatus("BOOKED");
        appointmentDto.setMedicationAdded(false);

        List<Doctor> doctors = doctorRepository.findAll();

        model.addAttribute("patientId", patientdto.getUserId());  // Ensure patient is added
        model.addAttribute("doctors", doctors);
        model.addAttribute("appointment", new AppointmentDto());

        return "appointment-booking";
    }


    @PostMapping("/book")
    public String bookAppointment(@Valid @ModelAttribute("appointment") AppointmentDto appointmentDto, BindingResult result, Model model) {
        if (appointmentDto.getStatus() == null || appointmentDto.getStatus().trim().isEmpty()) {
            appointmentDto.setStatus("BOOKED");
        }
        if (result.hasErrors()) {
//            result.rejectValue("appointmentTime","Appointment time must be in the present or future" );
            model.addAttribute("error", "The selected slot is no longer available. Please choose another.");
//            result.getAllErrors().forEach(error -> System.out.println("Validation Error: " + error.getDefaultMessage()));
            List<Doctor> doctors = doctorRepository.findAll();
            model.addAttribute("doctors", doctors);
            return "appointment-booking";
        }

        LocalDateTime selectedTime=appointmentDto.getAppointmentTime();
//        System.out.println("Selected Time: " + selectedTime);
//        System.out.println("Doctor ID: " + appointmentDto.getDoctorId());

        boolean isSlotAvailable=availabilityRepository.existsByDoctorIdAndAvailableSlot(appointmentDto.getDoctorId(),selectedTime);
        System.out.println("Is Slot Available: " + isSlotAvailable);
        if(!isSlotAvailable){
            model.addAttribute("error", "The selected slot is no longer available. Please choose another.");
            return "appointment-booking";
        }
        appointmentService.saveAppointment(appointmentDto);

        appointmentService.deleteAvailability(appointmentDto.getDoctorId(),selectedTime);
        return "redirect:/appointments/view";
    }

    @GetMapping("/view")
    public String viewAppointments(@RequestParam(required = false) Long doctorId, @RequestParam(required = false) Long patientId, Model model,Principal principal) {
        try {
            List<AppointmentDto> appointments;
            if (doctorId != null) {
                model.addAttribute("appointments", appointmentService.getAppointmentsByDoctor(doctorId));
            } else if (patientId != null) {
                model.addAttribute("appointments", appointmentService.getAppointmentsByPatient(patientId));
            }else {
                // If no doctor or patient ID is provided, fetch based on logged-in user
                User user = userRepository.findByEmail(principal.getName())
                        .orElseThrow(() -> new UsernameNotFoundException("User not found"));

                if (user.getRole().equals("DOCTOR")) {
                    Doctor doctor = doctorRepository.findByUserId(user.getId())
                            .orElseThrow(() -> new DoctorNotFoundException("Doctor not found"));
                    model.addAttribute("appointments", appointmentService.getAppointmentsByDoctor(doctor.getId()));
                    return "doctor-appointments";
                } else {
                    Patient patient = patientRepository.findByUserId(user.getId())
                            .orElseThrow(() -> new PatientNotFoundException("Patient not found"));
                    model.addAttribute("appointments", appointmentService.getAppointmentsByPatient(patient.getId()));
                }
            }
        } catch (DoctorNotFoundException | PatientNotFoundException ex) {
            model.addAttribute("error", ex.getMessage());
        }
        return "appointments-view";
    }

    @PostMapping("/cancel")
    public String cancelAppointment(@RequestParam Long appointmentId, Model model) {
        try{
            AppointmentDto appointmentDto=appointmentService.getAppointmentDetails(appointmentId);
            if(appointmentDto==null){
                throw new AppointmentNotFoundException("Appointment not found.");
            }
            appointmentService.cancelAppointment(appointmentId);

            Doctor doctor=doctorRepository.findById(appointmentDto.getDoctorId()).orElseThrow(()-> new DoctorNotFoundException("Doctor not found"));
            Availability restoredSlot=new Availability(appointmentDto.getAppointmentTime(),doctor);
            availabilityRepository.save(restoredSlot);
            model.addAttribute("message", "Appointment canceled successfully.");
            return "redirect:/appointments/view";
        }catch (DoctorNotFoundException ex){
            model.addAttribute("error", ex.getMessage());
            return "doctor-appointments";
        }
    }

//    @GetMapping("/details")
//    public String getAppointmentDetails(@RequestParam Long appointmentId, Model model) {
//        try {
//            AppointmentDto appointmentDto = appointmentService.getAppointmentDetails(appointmentId);
//            model.addAttribute("appointment", appointmentDto);
//        } catch (AppointmentNotFoundException ex) {
//            model.addAttribute("error", ex.getMessage());
//        }
//        return "appointment-details";
//    }
}

