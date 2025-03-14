package com.example.medical.restController;

import com.example.medical.dto.UserDto;
import com.example.medical.entity.User;
import com.example.medical.repository.UserRepository;
import com.example.medical.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserRestController {
        @Autowired
        UserDetailsService userDetailsService;

        @Autowired
        UserRepository userRepository;

        @Autowired
        private UserService userService;

        @PostMapping("/signup")
        public ResponseEntity<?> saveUser(@Valid @RequestBody UserDto userDto, BindingResult result){
            User existingUser = userRepository.findByEmail(userDto.getEmail()).orElse(null);
            if (existingUser != null) {
                return ResponseEntity.badRequest().body(Collections.singletonMap("error","Email already exists"));
            }

            if (result.hasErrors()) {
                Map<String,String> errors=new HashMap<>();
                result.getFieldErrors().forEach(error -> errors.put(error.getField(),error.getDefaultMessage()));
               return ResponseEntity.badRequest().body(errors);
            }

            userService.save(userDto);
            return ResponseEntity.ok("Registered successfully");

        }

        @GetMapping("/doctor-page")
        public ResponseEntity<?> doctorPage(Principal principal){
            UserDetails userDetails=userDetailsService.loadUserByUsername(principal.getName());
            return ResponseEntity.ok(userDetails);
        }

        @GetMapping("/patient-page")
        public ResponseEntity<?> patientPage(Principal principal){
            UserDetails userDetails=userDetailsService.loadUserByUsername(principal.getName());
            return ResponseEntity.ok(userDetails);
        }
}
