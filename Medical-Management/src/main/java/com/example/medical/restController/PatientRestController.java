package com.example.medical.restController;

import com.example.medical.dto.PatientDto;
import com.example.medical.dto.UserDto;
import com.example.medical.service.PatientService;
import com.example.medical.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/patients")
public class PatientRestController {
    @Autowired
    private PatientService patientService;

    @Autowired
    private UserService userService;

    @PostMapping("/save-details")
    public ResponseEntity<?> savePatientDetails(@Valid @RequestBody PatientDto patientDto, BindingResult result, Principal principal) {
        if (result.hasErrors()) {
            Map<String,String> errors=new HashMap<>();
            for(FieldError error: result.getFieldErrors()){
                errors.put(error.getField(),error.getDefaultMessage());
            }
            return ResponseEntity.badRequest().body(errors);
        }

        // Fetch logged-in user
        UserDto userDto = userService.findByEmail(principal.getName());
        if (userDto == null || userDto.getId() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found"); // Handle gracefully
        }

        // Set userId in PatientDto before saving
        patientDto.setUserId(userDto.getId());
        // Save patient details
        patientService.savePatientDetails(patientDto);

        Map<String,String> response=new HashMap<>();
        response.put("message","Patient details saved successfully");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/view-details")
    public ResponseEntity<?> getPatientDetails(Principal principal) {
        UserDto userDto = userService.findByEmail(principal.getName());
        if (userDto == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "User not found"));  // Prevent null issues
        }

        PatientDto patientDto = patientService.getPatientDetails(userDto.getId());
        if(patientDto == null){
            patientDto=new PatientDto();
            patientDto.setUserId(userDto.getId());
        }
        return ResponseEntity.ok(patientDto);
    }
}
