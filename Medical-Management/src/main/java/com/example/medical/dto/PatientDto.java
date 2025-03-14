package com.example.medical.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public class PatientDto {
    private Long id;

    @Min(value = 0, message = "Age must be positive")
    private Integer age;

    @NotBlank(message = "Gender is required")
    private String gender;

    private String medicalHistory;

    private Long userId;

    public PatientDto() {
    }

    public PatientDto(Long id, Integer age, String gender, String medicalHistory, Long userId) {
        this.id = id;
        this.age = age;
        this.gender = gender;
        this.medicalHistory = medicalHistory;
        this.userId = userId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public @Min(value = 0, message = "Age must be positive") Integer getAge() {
        return age;
    }

    public void setAge(@Min(value = 0, message = "Age must be positive") Integer age) {
        this.age = age;
    }

    public @NotBlank(message = "Gender is required") String getGender() {
        return gender;
    }

    public void setGender(@NotBlank(message = "Gender is required") String gender) {
        this.gender = gender;
    }

    public String getMedicalHistory() {
        return medicalHistory;
    }

    public void setMedicalHistory(String medicalHistory) {
        this.medicalHistory = medicalHistory;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
