package com.example.medical.service.impl;

import com.example.medical.dto.AppointmentDto;
import com.example.medical.dto.DoctorDto;
import com.example.medical.dto.PatientDto;
import com.example.medical.entity.*;
import com.example.medical.exceptions.DoctorNotFoundException;
import com.example.medical.repository.AppointmentRepository;
import com.example.medical.repository.AvailabilityRepository;
import com.example.medical.repository.DoctorRepository;
import com.example.medical.repository.UserRepository;
import com.example.medical.service.DoctorService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DoctorServiceImpl implements DoctorService {

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AvailabilityRepository availabilityRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public DoctorDto saveDoctorDetails(DoctorDto doctorDto) {
        Optional<Doctor> existingDoctor=doctorRepository.findByUserId(doctorDto.getUserId());
        Doctor doctor;
        if (existingDoctor.isPresent()){
            doctor=existingDoctor.get();
        }else {
            doctor=new Doctor();
            User user=userRepository.findById(doctorDto.getUserId())
                    .orElseThrow(()-> new RuntimeException("User not found with ID: " + doctorDto.getUserId()));
            doctor.setUser(user);
        }

        doctor.setSpecialization(doctorDto.getSpecialization());
        doctor.setContactNumber(doctorDto.getContactNumber());

        Doctor savedDoctor=doctorRepository.saveAndFlush(doctor);

        return modelMapper.map(savedDoctor,DoctorDto.class);
    }

    @Override
    public List<AppointmentDto> viewAppointments(Long doctorId) {
        // Validate doctor existence
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new DoctorNotFoundException("Doctor with ID " + doctorId + " not found"));

        // Fetch and map appointments
        List<Appointment> appointments = appointmentRepository.findByDoctor(doctor);

        return appointments.stream()
                .map(appointment -> modelMapper.map(appointment, AppointmentDto.class))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void updateAvailability(Long doctorId, List<LocalDateTime> availableSlots) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new DoctorNotFoundException("Doctor with ID " + doctorId + " not found"));

        availabilityRepository.deleteByDoctorId(doctorId);

        // Add new slots
        List<Availability> availabilityList = availableSlots.stream()
                .map(slot -> new Availability(slot, doctor))
                .collect(Collectors.toList());

        availabilityRepository.saveAll(availabilityList);
    }

    @Override
    public DoctorDto getDoctorDetails(Long doctorId) {
        Optional<Doctor> doctor=doctorRepository.findByUserId(doctorId);
        return doctor.map(value -> modelMapper.map(value, DoctorDto.class)).orElse(null);
    }
}
