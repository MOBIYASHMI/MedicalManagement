package com.example.medical.entity;

import jakarta.persistence.*;

@Entity
@Table(name="medication")
public class Medication {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String medicineName;
    @Column(nullable = false)
    private String dosage;
    @Column(nullable = false)
    private String instruction;

    @ManyToOne
    @JoinColumn(name = "appointment_id",nullable = false)
    private Appointment appointment;

    public Medication() {
    }

    public Medication(Long id, String medicineName, String dosage, String instruction, Appointment appointment) {
        this.id = id;
        this.medicineName = medicineName;
        this.dosage = dosage;
        this.instruction = instruction;
        this.appointment = appointment;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMedicineName() {
        return medicineName;
    }

    public void setMedicineName(String medicineName) {
        this.medicineName = medicineName;
    }

    public String getDosage() {
        return dosage;
    }

    public void setDosage(String dosage) {
        this.dosage = dosage;
    }

    public String getInstruction() {
        return instruction;
    }

    public void setInstruction(String instruction) {
        this.instruction = instruction;
    }

    public Appointment getAppointment() {
        return appointment;
    }

    public void setAppointment(Appointment appointment) {
        this.appointment = appointment;
    }
}
