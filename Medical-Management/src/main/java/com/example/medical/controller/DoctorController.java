package com.example.medical.controller;

import com.example.medical.dto.AppointmentDto;
import com.example.medical.dto.DoctorDto;
import com.example.medical.dto.UserDto;
import com.example.medical.exceptions.DoctorNotFoundException;
import com.example.medical.service.DoctorService;
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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/doctors")
public class DoctorController {

    @Autowired
    private DoctorService doctorService;

    @Autowired
    private UserService userService;

    @PostMapping("/save-details")
    public String saveDoctorDetails(@Valid @ModelAttribute("doctor") DoctorDto doctorDto, BindingResult result, RedirectAttributes redirectAttributes, Principal principal) {
        if (result.hasErrors()) {
            return "doctor-details";
        }

        UserDto userDto=userService.findByEmail(principal.getName());
        if(userDto==null || userDto.getId()==null){
            throw new UsernameNotFoundException("User not found");
        }

        doctorDto.setUserId(userDto.getId());
        doctorService.saveDoctorDetails(doctorDto);
        redirectAttributes.addFlashAttribute("message", "Doctor details saved successfully.");
        return "redirect:/doctors/view-details";
    }

    @GetMapping("/update-availability")
    public String getUpdateAvailability(Model model,Principal principal) {
        UserDto userDto = userService.findByEmail(principal.getName());
        if (userDto == null) {
            userDto = new UserDto();  // Prevent null issues
        }
        DoctorDto doctorDto = doctorService.getDoctorDetails(userDto.getId());
        if(doctorDto == null){
            doctorDto=new DoctorDto();
            doctorDto.setUserId(userDto.getId());
        }
        System.out.println(doctorDto.getId());
        model.addAttribute("user", userDto);
        model.addAttribute("doctor", doctorDto);
        return "availability-slots";
    }

    @PostMapping("/save-availability")
    public String updateAvailability(@RequestParam Long doctorId, @RequestParam List<String> availableSlots, RedirectAttributes redirectAttributes,Model model) {
        try {
            List<LocalDateTime> slotList=availableSlots.stream().map(slot -> LocalDateTime.parse(slot, DateTimeFormatter.ISO_DATE_TIME))
                            .collect(Collectors.toList());
            doctorService.updateAvailability(doctorId, slotList);
            redirectAttributes.addFlashAttribute("message", "Availability updated successfully.");
        } catch (DoctorNotFoundException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/doctor-page";
    }

    @GetMapping("/view-details")
    public String getDoctorDetails(Model model, Principal principal) {
        UserDto userDto = userService.findByEmail(principal.getName());
        if (userDto == null) {
            userDto = new UserDto();  // Prevent null issues
        }

        DoctorDto doctorDto = doctorService.getDoctorDetails(userDto.getId());
        if(doctorDto == null){
            doctorDto=new DoctorDto();
            doctorDto.setUserId(userDto.getId());
        }
        model.addAttribute("user", userDto);
        model.addAttribute("doctor", doctorDto);
        return "doctor-details";
    }
}
