package com.example.medical.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="doctor")
public class Doctor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String specialization;

    @Column(nullable = false)
    private String contactNumber;

    @OneToOne
    @JoinColumn(name = "user_id",nullable=false)
    private User user;

    @OneToMany(mappedBy = "doctor",cascade = CascadeType.ALL,orphanRemoval = true)
    List<Appointment> appointments=new ArrayList<>();

//    @OneToMany(mappedBy = "doctor", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<Availability> availableSlots = new ArrayList<>();

    public Doctor() {
    }

    public Doctor(Long id, String specialization, String contactNumber, User user, List<Appointment> appointments, List<Availability> availableSlots) {
        this.id = id;
        this.specialization = specialization;
        this.contactNumber = contactNumber;
        this.user = user;
        this.appointments = appointments;
//        this.availableSlots = availableSlots;
    }

    public Doctor(@NotNull(message = "Doctor ID is required") Long doctorId) {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<Appointment> getAppointments() {
        return appointments;
    }

    public void setAppointments(List<Appointment> appointments) {
        this.appointments = appointments;
    }

//    public List<Availability> getAvailableSlots() {
//        return availableSlots;
//    }
//
//    public void setAvailableSlots(List<Availability> availableSlots) {
//        this.availableSlots = availableSlots;
//    }
}
