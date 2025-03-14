package com.example.medical.service;

import com.example.medical.dto.AppointmentDto;
import com.example.medical.dto.DoctorDto;
import com.example.medical.dto.MedicationDto;

import java.time.LocalDateTime;
import java.util.List;

public interface DoctorService {
    DoctorDto saveDoctorDetails(DoctorDto doctorDto);
    List<AppointmentDto> viewAppointments(Long doctorId);
    void updateAvailability(Long doctorId, List<LocalDateTime> availableSlots);
    DoctorDto getDoctorDetails(Long doctorId);
}
