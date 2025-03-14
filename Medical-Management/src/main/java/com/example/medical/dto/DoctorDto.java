package com.example.medical.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;

import java.util.List;

public class DoctorDto {
    private Long id;

    @NotBlank(message = "Specialization is required")
    private String specialization;

    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Invalid contact number")
    private String contactNumber;

    private Long userId;

//    @NotEmpty(message = "Mention the availability")
//    private List<AvailabilityDto> availableSlots;

    public DoctorDto() {
    }

    public DoctorDto(Long id, String specialization, String contactNumber, Long userId, List<AvailabilityDto> availableSlots) {
        this.id = id;
        this.specialization = specialization;
        this.contactNumber = contactNumber;
        this.userId = userId;
//        this.availableSlots = availableSlots;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public @NotBlank(message = "Specialization is required") String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(@NotBlank(message = "Specialization is required") String specialization) {
        this.specialization = specialization;
    }

    public @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Invalid contact number") String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(@Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Invalid contact number") String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

//    public List<AvailabilityDto> getAvailableSlots() {
//        return availableSlots;
//    }
//
//    public void setAvailableSlots(List<AvailabilityDto> availableSlots) {
//        this.availableSlots = availableSlots;
//    }
}
