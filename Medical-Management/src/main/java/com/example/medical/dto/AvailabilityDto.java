package com.example.medical.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public class AvailabilityDto {

    private Long id;
    @NotNull(message = "Available slot is required")
    @Future(message = "Appointment time must be in the future")
    private LocalDateTime availableSlot;
    @NotNull(message = "Doctor ID is required")
    private Long doctorId;

    public AvailabilityDto() {}

    public AvailabilityDto(Long id, LocalDateTime availableSlot, Long doctorId) {
        this.id = id;
        this.availableSlot = availableSlot;
        this.doctorId = doctorId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getAvailableSlot() {
        return availableSlot;
    }

    public void setAvailableSlot(LocalDateTime availableSlot) {
        this.availableSlot = availableSlot;
    }

    public Long getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(Long doctorId) {
        this.doctorId = doctorId;
    }
}
