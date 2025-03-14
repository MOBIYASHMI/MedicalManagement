package com.example.medical.service.impl;

import com.example.medical.dto.AvailabilityDto;
import com.example.medical.entity.Availability;
import com.example.medical.entity.Doctor;
import com.example.medical.exceptions.DoctorNotFoundException;
import com.example.medical.repository.AvailabilityRepository;
import com.example.medical.repository.DoctorRepository;
import com.example.medical.service.AvailabilityService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AvailabilityServiceImpl implements AvailabilityService {

    @Autowired
    private AvailabilityRepository availabilityRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public void addAvailability(Long doctorId, List<AvailabilityDto> availabilitySlots) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new DoctorNotFoundException("Doctor not found with ID: " + doctorId));

        // Map DTOs to entities and associate with doctor
        List<Availability> availabilityList = availabilitySlots.stream()
                .map(slot -> {
                    Availability availability = modelMapper.map(slot, Availability.class);
                    availability.setDoctor(doctor);
                    return availability;
                })
                .collect(Collectors.toList());

        availabilityRepository.saveAll(availabilityList);
    }

    @Override
    public List<AvailabilityDto> getAvailabilityByDoctor(Long doctorId) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new DoctorNotFoundException("Doctor not found with ID: " + doctorId));

        return availabilityRepository.findByDoctor(doctor).stream()
                .map(availability -> modelMapper.map(availability, AvailabilityDto.class))
                .collect(Collectors.toList());
    }
//
//    @Override
//    public void deleteAvailability(Long availabilityId) {
//        Availability availability = availabilityRepository.findById(availabilityId)
//                .orElseThrow(() -> new AvailabilityNotFoundException("Availability not found with ID: " + availabilityId));
//
//        availabilityRepository.delete(availability);
//    }
}

