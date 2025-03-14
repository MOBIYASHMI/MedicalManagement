package com.example.medical.restController;

import com.example.medical.dto.AppointmentDto;
import com.example.medical.dto.MedicationDto;
import com.example.medical.entity.Doctor;
import com.example.medical.entity.Patient;
import com.example.medical.entity.User;
import com.example.medical.exceptions.DoctorNotFoundException;
import com.example.medical.exceptions.PatientNotFoundException;
import com.example.medical.repository.DoctorRepository;
import com.example.medical.repository.PatientRepository;
import com.example.medical.repository.UserRepository;
import com.example.medical.service.AppointmentService;
import com.example.medical.service.MedicationService;
import com.example.medical.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/api/medications")
public class MedicationRestController {
    @Autowired
    private MedicationService medicationService;

    @Autowired
    private UserService userService;

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private PatientRepository patientRepository;

    @GetMapping("/medicationList")
    public ResponseEntity<?> showMedication(){
        List<String> predefinedMedicines=List.of("Paracetamol", "Ibuprofen", "Amoxicillin", "Cetirizine", "Aspirin");
        Map<String, Object> response=new HashMap<>();
        response.put("medicineList",predefinedMedicines);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/add")
    public ResponseEntity<?> addMedication(@RequestBody MedicationDto medicationDto){
        if(medicationDto.getAppointmentId()==null){
            return ResponseEntity.badRequest().body("Appointment ID is required");
        }
        String medicineNames = (medicationDto.getMedicineName() != null && !medicationDto.getMedicineName().isEmpty())
                ? String.join(",", medicationDto.getMedicineName())
                : "No Medication Prescribed";
        MedicationDto existingMedicationDto=medicationService.getMedicationsByAppointment(medicationDto.getAppointmentId());
        if(existingMedicationDto !=null){
            existingMedicationDto.setMedicineName(medicineNames);
            existingMedicationDto.setDosage(medicationDto.getDosage());
            existingMedicationDto.setInstruction(medicationDto.getInstruction());
            medicationService.updateMedication(existingMedicationDto);
            return ResponseEntity.ok("Medication updated successfully");
        }else{
            medicationDto.setMedicineName(medicineNames);
            medicationService.addMedication(medicationDto);
            return ResponseEntity.status(HttpStatus.CREATED).body("Medication added successfully");
        }
    }

    @GetMapping("/view")
    public ResponseEntity<?> viewMedications(@RequestParam Long appointmentId,Principal principal) {
        User user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        MedicationDto medications = medicationService.getMedicationsByAppointment(appointmentId);

        if (medications == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No medication found for this appointment.");
        }
        return ResponseEntity.ok(medications);
    }

    @PostMapping("/update")
    public ResponseEntity<?> updateMedication(@Valid @RequestBody MedicationDto medicationDto, BindingResult result) {
        if (result.hasErrors()) {
            Map<String,String> errors=new HashMap<>();
            result.getFieldErrors().forEach(error -> errors.put(error.getField(),error.getDefaultMessage()));
            return ResponseEntity.badRequest().body(errors);
        }
        medicationService.updateMedication(medicationDto);
        return ResponseEntity.ok().body("Medication updated successfully.");
    }

    @PostMapping("/delete")
    public ResponseEntity<?> deleteMedication(@RequestParam Long appointmentId) {
        medicationService.deleteMedication(appointmentId);
        return ResponseEntity.ok("Deleted successfully");
    }
}
