package com.example.medical.service;

import com.example.medical.dto.AvailabilityDto;

import java.util.List;

public interface AvailabilityService {

    // Add available slots for a doctor
    void addAvailability(Long doctorId, List<AvailabilityDto> availabilitySlots);

    // Get all available slots for a doctor
    List<AvailabilityDto> getAvailabilityByDoctor(Long doctorId);

    // Remove an availability slot by its ID
//    void deleteAvailability(Long availabilityId);
}
