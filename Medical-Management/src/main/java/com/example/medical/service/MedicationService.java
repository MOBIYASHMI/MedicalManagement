package com.example.medical.service;

import com.example.medical.dto.MedicationDto;

import java.util.List;

public interface MedicationService {
    MedicationDto addMedication(MedicationDto medicationDto);
    MedicationDto getMedicationsByAppointment(Long appointmentId);
    void updateMedication(MedicationDto medicationDto);
    void deleteMedication(Long medicationId);
    List<MedicationDto> viewPrescribedMedications(Long doctorId);
}
