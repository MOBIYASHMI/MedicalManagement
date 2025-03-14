package com.example.medical.repository;

import com.example.medical.entity.Medication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MedicationRepository extends JpaRepository<Medication,Long> {

    Optional<Medication> findByAppointmentId(Long appointmentId);
}
