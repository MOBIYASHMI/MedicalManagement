package com.example.medical.entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="patient")
public class Patient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer age;
    @Column(nullable = false)
    private String gender;

    private String medicalHistory;

    @OneToOne
    @JoinColumn(name = "user_id",referencedColumnName = "id",nullable=false)
    private User user;

    @OneToMany(mappedBy = "patient",cascade = CascadeType.ALL,orphanRemoval = true)
    List<Appointment> appointments=new ArrayList<>();

    public Patient() {
    }

    public Patient(Long id, Integer age, String gender, String medicalHistory, User user, List<Appointment> appointments) {
        this.id = id;
        this.age = age;
        this.gender = gender;
        this.medicalHistory = medicalHistory;
        this.user = user;
        this.appointments = appointments;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getMedicalHistory() {
        return medicalHistory;
    }

    public void setMedicalHistory(String medicalHistory) {
        this.medicalHistory = medicalHistory;
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


}
