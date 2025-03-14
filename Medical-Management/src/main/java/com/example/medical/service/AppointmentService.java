package com.example.medical.service;

import com.example.medical.dto.AppointmentDto;

import java.time.LocalDateTime;
import java.util.List;

public interface AppointmentService {
    AppointmentDto saveAppointment(AppointmentDto appointmentDto);
    void deleteAvailability(Long doctorId, LocalDateTime selectedTime);
    List<AppointmentDto> getAppointmentsByDoctor(Long doctorId);
    List<AppointmentDto> getAppointmentsByPatient(Long patientId);
    void cancelAppointment(Long appointmentId);
    AppointmentDto getAppointmentDetails(Long appointmentId);
}
