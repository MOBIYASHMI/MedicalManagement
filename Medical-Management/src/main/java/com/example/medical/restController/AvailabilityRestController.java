package com.example.medical.restController;

import com.example.medical.dto.AvailabilityDto;
import com.example.medical.service.AvailabilityService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/availability")
public class AvailabilityRestController {

    @Autowired
    private AvailabilityService availabilityService;

    @PostMapping("/add")
    public ResponseEntity<?> addAvailability(@RequestParam Long doctorId, @Valid @RequestBody AvailabilityDto availabilityDto, BindingResult result) {
        if (result.hasErrors()) {
            Map<String,String> errors=new HashMap<>();
            result.getFieldErrors().forEach(error -> errors.put(error.getField(),error.getDefaultMessage()));
            return ResponseEntity.badRequest().body(errors);
        }
        availabilityService.addAvailability(doctorId, List.of(availabilityDto));
        return ResponseEntity.ok().body(Collections.singletonMap("message", "Availability added successfully."));
    }

    @GetMapping("/available-slots")
    @ResponseBody
    public List<AvailabilityDto> getAvailableSlots(@RequestParam Long doctorId) {
        return availabilityService.getAvailabilityByDoctor(doctorId);
    }
}
