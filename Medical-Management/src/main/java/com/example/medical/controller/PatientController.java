package com.example.medical.controller;

import com.example.medical.dto.PatientDto;
import com.example.medical.dto.UserDto;
import com.example.medical.service.PatientService;
import com.example.medical.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
@RequestMapping("/patients")
public class PatientController {

    @Autowired
    private PatientService patientService;

    @Autowired
    private UserService userService;

    @PostMapping("/save-details")
    public String savePatientDetails(@Valid @ModelAttribute("patient") PatientDto patientDto, BindingResult result, RedirectAttributes redirectAttributes, Principal principal) {
        if (result.hasErrors()) {
            return "patient-details";
        }

        // Fetch logged-in user
        UserDto userDto = userService.findByEmail(principal.getName());
        if (userDto == null || userDto.getId() == null) {
            throw new UsernameNotFoundException("User not found"); // Handle gracefully
        }

        // Set userId in PatientDto before saving
        patientDto.setUserId(userDto.getId());
        // Save patient details
        patientService.savePatientDetails(patientDto);

        redirectAttributes.addFlashAttribute("message", "Patient details saved successfully.");
        return "redirect:/patients/view-details";
    }

    @GetMapping("/view-details")
    public String getPatientDetails(Model model, Principal principal) {
        UserDto userDto = userService.findByEmail(principal.getName());
        if (userDto == null) {
            userDto = new UserDto();  // Prevent null issues
        }

        PatientDto patientDto = patientService.getPatientDetails(userDto.getId());
        if(patientDto == null){
            patientDto=new PatientDto();
            patientDto.setUserId(userDto.getId());
        }
        model.addAttribute("user", userDto);
        model.addAttribute("patient", patientDto);
        return "patient-details";
    }

}
