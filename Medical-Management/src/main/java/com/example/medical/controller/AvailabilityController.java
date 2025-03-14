package com.example.medical.controller;

import com.example.medical.dto.AvailabilityDto;
import com.example.medical.entity.Doctor;
import com.example.medical.exceptions.DoctorNotFoundException;
import com.example.medical.repository.DoctorRepository;
import com.example.medical.service.AvailabilityService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/availability")
public class AvailabilityController {

    @Autowired
    private AvailabilityService availabilityService;

    @Autowired
    private DoctorRepository doctorRepository;

    @PostMapping("/add")
    public String addAvailability(@RequestParam Long doctorId, @Valid @ModelAttribute("availability") AvailabilityDto availabilityDto, BindingResult result, RedirectAttributes redirectAttributes,Model model) {
        if (result.hasErrors()) {
            Doctor doctor = new Doctor();
            doctor.setId(doctorId); // Avoids Thymeleaf error doctor.id
            model.addAttribute("doctor", doctor);
            model.addAttribute("error",result.hasErrors());
            return "availability-slots";
        }
        availabilityService.addAvailability(doctorId, List.of(availabilityDto));
        redirectAttributes.addFlashAttribute("message", "Availability added successfully.");
        return "redirect:/doctors/update-availability";
    }

//    @GetMapping("/view")
//    public String viewAvailability(@RequestParam Long doctorId, Model model) {
//        List<AvailabilityDto> availabilitySlots = availabilityService.getAvailabilityByDoctor(doctorId);
//        model.addAttribute("availabilitySlots", availabilitySlots);
//        return "availability-view";
//    }

    @GetMapping("/available-slots")
    @ResponseBody
    public List<AvailabilityDto> getAvailableSlots(@RequestParam Long doctorId) {
        return availabilityService.getAvailabilityByDoctor(doctorId);
    }

//    @PostMapping("/delete")
//    public String deleteAvailability(@RequestParam Long availabilityId, Model model) {
//        availabilityService.deleteAvailability(availabilityId);
//        model.addAttribute("message", "Availability deleted successfully.");
//        return "redirect:/availability/view";
//    }
}

