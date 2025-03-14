package com.example.medical.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;

public class AppointmentDto {
    private Long id;

    @NotNull(message = "Doctor ID is required")
    private Long doctorId;

    @NotNull(message = "Patient ID is required")
    private Long patientId;

    @FutureOrPresent(message = "Appointment time must be in the present or future")
    private LocalDateTime appointmentTime;

    private String status;


    private List<MedicationDto> medications;

    private String doctorName;  // New field
    private String doctorSpecialization; // New field
    private String patientName;

    private Boolean medicationAdded;

    public AppointmentDto() {
    }

    public AppointmentDto(Long id, Long doctorId, Long patientId, LocalDateTime appointmentTime, String status, List<MedicationDto> medications, String doctorName, String doctorSpecialization, String patientName, boolean medicationAdded) {
        this.id = id;
        this.doctorId = doctorId;
        this.patientId = patientId;
        this.appointmentTime = appointmentTime;
        this.status = status;
        this.medications = medications;
        this.doctorName = doctorName;
        this.doctorSpecialization = doctorSpecialization;
        this.patientName = patientName;
        this.medicationAdded = medicationAdded;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public @NotNull(message = "Doctor ID is required") Long getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(@NotNull(message = "Doctor ID is required") Long doctorId) {
        this.doctorId = doctorId;
    }

    public @NotNull(message = "Patient ID is required") Long getPatientId() {
        return patientId;
    }

    public void setPatientId(@NotNull(message = "Patient ID is required") Long patientId) {
        this.patientId = patientId;
    }

    public @FutureOrPresent(message = "Appointment time must be in the present or future") LocalDateTime getAppointmentTime() {
        return appointmentTime;
    }

    public void setAppointmentTime(@FutureOrPresent(message = "Appointment time must be in the present or future") LocalDateTime appointmentTime) {
        this.appointmentTime = appointmentTime;
    }

    public @NotBlank(message = "Status is required") String getStatus() {
        return status;
    }

    public void setStatus(@NotBlank(message = "Status is required") String status) {
        this.status = status;
    }

    public List<MedicationDto> getMedications() {
        return medications;
    }

    public void setMedications(List<MedicationDto> medications) {
        this.medications = medications;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public String getDoctorSpecialization() {
        return doctorSpecialization;
    }

    public void setDoctorSpecialization(String doctorSpecialization) {
        this.doctorSpecialization = doctorSpecialization;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public boolean isMedicationAdded() {
        return medicationAdded;
    }

    public void setMedicationAdded(boolean medicationAdded) {
        this.medicationAdded = medicationAdded;
    }
}
