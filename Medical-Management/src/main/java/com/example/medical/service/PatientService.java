package com.example.medical.service;

import com.example.medical.dto.PatientDto;

public interface PatientService {
    PatientDto savePatientDetails(PatientDto patientDto);

    // View patient details by their ID
    PatientDto getPatientDetails(Long patientId);
}
