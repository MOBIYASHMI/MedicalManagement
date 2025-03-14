package com.example.medical.controller;

import com.example.medical.dto.UserDto;
import com.example.medical.entity.User;
import com.example.medical.repository.UserRepository;
import com.example.medical.service.PatientService;
import com.example.medical.service.UserService;
import com.example.medical.service.impl.user.CustomUserDetail;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.security.Principal;

@Controller
public class UserController {
    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PatientService patientService;
    @Autowired
    private UserService userService;

    @GetMapping("/signup")
    public String signUpPage(Model model){
        model.addAttribute("user",new UserDto());
        return "signup";
    }

    @PostMapping("/signup")
    public String saveUser(@Valid @ModelAttribute("user") UserDto userDto, BindingResult result, Model model){
        User existingUser = userRepository.findByEmail(userDto.getEmail()).orElse(null);
        if (existingUser != null) {
            result.rejectValue("email", null, "Email already exists");
        }

        if (result.hasErrors()) {
            model.addAttribute("user", userDto);
            return "signup";
        }

        userService.save(userDto);
        model.addAttribute("message", "Registered successfully");
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String login(){
        return "login";
    }

    @GetMapping("/doctor-page")
    public String doctorPage(Model model, Principal principal){
//        UserDetails userDetails=userDetailsService.loadUserByUsername(principal.getName());
//        model.addAttribute("user",userDetails);
//        return "doctor-page";

        CustomUserDetail userDetail= (CustomUserDetail) userDetailsService.loadUserByUsername(principal.getName());
        User user=userDetail.getUser();
        model.addAttribute("user",user);
        return "doctor-page";


    }
    @GetMapping("/patient-page")
    public String patientPage(Model model, Principal principal){
//        UserDetails userDetails=userDetailsService.loadUserByUsername(principal.getName());
//        model.addAttribute("user",userDetails);
//        return "patient-page";

        CustomUserDetail userDetail= (CustomUserDetail) userDetailsService.loadUserByUsername(principal.getName());
        User user=userDetail.getUser();
        model.addAttribute("user",user);
        return "patient-page";
    }
}
