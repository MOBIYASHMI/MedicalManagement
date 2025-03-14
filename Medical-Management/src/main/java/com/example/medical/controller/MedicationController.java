package com.example.medical.controller;

import com.example.medical.dto.AppointmentDto;
import com.example.medical.dto.MedicationDto;
import com.example.medical.entity.Doctor;
import com.example.medical.entity.Medication;
import com.example.medical.entity.Patient;
import com.example.medical.entity.User;
import com.example.medical.exceptions.DoctorNotFoundException;
import com.example.medical.exceptions.MedicationNotFoundException;
import com.example.medical.exceptions.PatientNotFoundException;
import com.example.medical.repository.DoctorRepository;
import com.example.medical.repository.PatientRepository;
import com.example.medical.repository.UserRepository;
import com.example.medical.service.AppointmentService;
import com.example.medical.service.MedicationService;
import com.example.medical.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/medications")
public class MedicationController {

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

    @GetMapping("/add")
    public  String showMedication(@RequestParam Long appointmentId,Model model,Principal principal){
        List<String> predefinedMedicines=List.of("Paracetamol", "Ibuprofen", "Amoxicillin", "Cetirizine", "Aspirin");
        model.addAttribute("medicineList",predefinedMedicines);
        model.addAttribute("medication",new MedicationDto());
        model.addAttribute("appointmentId",appointmentId);
//        UserDto userDto=userService.findByEmail(principal.getName());
        return "medication-form";
    }

//    @PostMapping("/add")
//    public String addMedication(@Valid @ModelAttribute("medication") MedicationDto medicationDto, BindingResult result, Model model) {
//        if (result.hasErrors()) {
//            return "medication-form";
//        }
//        medicationService.addMedication(medicationDto);
//        model.addAttribute("message", "Medication added successfully.");
//        return "redirect:/medications/view";
//    }

    @PostMapping("/add")
    public String addMedication(@RequestParam("appointmentId") Long appointmentId, @RequestParam(value = "medicineName",required = false) List<String> selectedMedicines, @RequestParam(value = "dosage",required = false) String dosage, @RequestParam(value = "instruction",required = false) String instruction, RedirectAttributes redirectAttributes){
        String medicineNames = (selectedMedicines != null && !selectedMedicines.isEmpty())
                ? String.join(",", selectedMedicines)
                : "No Medication Prescribed";
        System.out.println(appointmentId);
        MedicationDto existingMedicationDto=medicationService.getMedicationsByAppointment(appointmentId);
        if(existingMedicationDto !=null){
//            existingMedicationDto.setAppointmentId(appointmentId);
            existingMedicationDto.setMedicineName(medicineNames);
            existingMedicationDto.setDosage(dosage);
            existingMedicationDto.setInstruction(instruction);
            medicationService.updateMedication(existingMedicationDto);
        }else{
            MedicationDto medicationDto=new MedicationDto();
            medicationDto.setAppointmentId(appointmentId);
            medicationDto.setMedicineName(medicineNames);
            medicationDto.setDosage(dosage);
            medicationDto.setInstruction(instruction);
            medicationService.addMedication(medicationDto);
        }
        redirectAttributes.addFlashAttribute("appointmentId",appointmentId);
        return "redirect:/medications/view";
    }

    @GetMapping("/view")
    public String viewMedications(@ModelAttribute("appointmentId") Long appointmentId,RedirectAttributes redirectAttributes, Model model,Principal principal) {
//        System.out.println(principal.getName());
        try{
            User user = userRepository.findByEmail(principal.getName())
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            MedicationDto medications = medicationService.getMedicationsByAppointment(appointmentId);
            model.addAttribute("medications", medications);

            if (user.getRole().equals("DOCTOR")) {
                Doctor doctor = doctorRepository.findByUserId(user.getId())
                        .orElseThrow(() -> new DoctorNotFoundException("Doctor not found"));
                model.addAttribute("appointments", appointmentService.getAppointmentsByDoctor(doctor.getId()));
                return "view-medication";
            } else {
                Patient patient = patientRepository.findByUserId(user.getId())
                        .orElseThrow(() -> new PatientNotFoundException("Patient not found"));
                model.addAttribute("appointments", appointmentService.getAppointmentsByPatient(patient.getId()));
            }

            return "medications-view";
        }catch (Exception ex){
            redirectAttributes.addFlashAttribute("error",ex.getMessage());
            return "redirect:/medications/view";
        }
    }

//    @PostMapping("/update")
//    public String updateMedication(@Valid @ModelAttribute("medication") MedicationDto medicationDto, BindingResult result, Model model) {
//        if (result.hasErrors()) {
//            return "medication-form";
//        }
//        medicationService.updateMedication(medicationDto);
//        model.addAttribute("message", "Medication updated successfully.");
//        return "redirect:/medications/view";
//    }

    @PostMapping("/delete")
    public String deleteMedication(@RequestParam Long appointmentId,RedirectAttributes redirectAttributes) {
//        MedicationDto medication=medicationService.getMedicationsByAppointment(appointmentId);
        medicationService.deleteMedication(appointmentId);
        return "redirect:/appointments/view";
    }
}
