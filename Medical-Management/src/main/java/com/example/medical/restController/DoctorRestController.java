package com.example.medical.restController;

import com.example.medical.dto.DoctorDto;
import com.example.medical.dto.UserDto;
import com.example.medical.exceptions.DoctorNotFoundException;
import com.example.medical.service.DoctorService;
import com.example.medical.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/doctors")
public class DoctorRestController {

    @Autowired
    private DoctorService doctorService;

    @Autowired
    private UserService userService;

    @PostMapping("/save-details")
    public ResponseEntity<?> saveDoctorDetails(@Valid @RequestBody DoctorDto doctorDto, BindingResult result,Principal principal) {
        if (result.hasErrors()) {
            Map<String,String> errors=new HashMap<>();
            result.getFieldErrors().forEach(error -> errors.put(error.getField(),error.getDefaultMessage()));
            return ResponseEntity.badRequest().body(errors);
        }

        UserDto userDto=userService.findByEmail(principal.getName());
        if(userDto==null || userDto.getId()==null){
            return ResponseEntity.badRequest().body("User not found");
        }

        doctorDto.setUserId(userDto.getId());
        doctorService.saveDoctorDetails(doctorDto);
        return ResponseEntity.ok( "Doctor details saved successfully.");
    }

    @GetMapping("/view-details")
    public ResponseEntity<?> getPatientDetails(Model model, Principal principal) {
        UserDto userDto = userService.findByEmail(principal.getName());
        if (userDto == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");  // Prevent null issues
        }

        DoctorDto doctorDto = doctorService.getDoctorDetails(userDto.getId());
        if(doctorDto == null){
            doctorDto=new DoctorDto();
            doctorDto.setUserId(userDto.getId());
        }
        return ResponseEntity.ok().body(doctorDto);
    }

    @GetMapping("/update-availability")
    public ResponseEntity<?> updateAvailability(Principal principal) {
        UserDto userDto = userService.findByEmail(principal.getName());
        if (userDto == null) {
            return ResponseEntity.badRequest().body("User not found");  // Prevent null issues
        }
        DoctorDto doctorDto = doctorService.getDoctorDetails(userDto.getId());
        if(doctorDto == null){
            doctorDto=new DoctorDto();
            doctorDto.setUserId(userDto.getId());
        }
        return ResponseEntity.ok().body(doctorDto);
    }

    @PostMapping("/save-availability")
    public ResponseEntity<?> updateAvailability(@RequestParam Long doctorId, @RequestParam List<String> availableSlots, Model model) {
        try {
            List<LocalDateTime> slotList=availableSlots.stream().map(slot -> LocalDateTime.parse(slot, DateTimeFormatter.ISO_DATE_TIME))
                    .collect(Collectors.toList());
            doctorService.updateAvailability(doctorId, slotList);
        } catch (DoctorNotFoundException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
        return ResponseEntity.ok("Availability updated successfully.");
    }
}
