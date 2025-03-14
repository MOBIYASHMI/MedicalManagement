package com.example.medical.dto;

import jakarta.validation.constraints.NotBlank;

public class MedicationDto {
    private Long id;

    @NotBlank(message = "Medication name is required")
    private String medicineName;

    @NotBlank(message = "Dosage is required")
    private String dosage;

    @NotBlank(message = "Instruction is required")
    private String instruction;

    private Long appointmentId;

    public MedicationDto() {
    }

    public MedicationDto(Long id, String medicineName, String dosage, String instruction, Long appointmentId) {
        this.id = id;
        this.medicineName = medicineName;
        this.dosage = dosage;
        this.instruction = instruction;
        this.appointmentId = appointmentId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public @NotBlank(message = "Medication name is required") String getMedicineName() {
        return medicineName;
    }

    public void setMedicineName(@NotBlank(message = "Medication name is required") String medicineName) {
        this.medicineName = medicineName;
    }

    public @NotBlank(message = "Dosage is required") String getDosage() {
        return dosage;
    }

    public void setDosage(@NotBlank(message = "Dosage is required") String dosage) {
        this.dosage = dosage;
    }

    public @NotBlank(message = "Instruction is required") String getInstruction() {
        return instruction;
    }

    public void setInstruction(@NotBlank(message = "Instruction is required") String instruction) {
        this.instruction = instruction;
    }

    public Long getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(Long appointmentId) {
        this.appointmentId = appointmentId;
    }
}
