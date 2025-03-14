package com.example.medical.repository;

import com.example.medical.entity.Availability;
import com.example.medical.entity.Doctor;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AvailabilityRepository extends JpaRepository<Availability, Long> {
    List<Availability> findByDoctor(Doctor doctor);

    boolean existsByDoctorIdAndAvailableSlot(Long doctorId, LocalDateTime availableSlot);

    @Modifying
    @org.springframework.transaction.annotation.Transactional
    @Query("DELETE FROM Availability a WHERE a.doctor.id = :doctorId")
    void deleteByDoctorId(@Param("doctorId") Long doctorId);

    @Modifying
    @Query("DELETE FROM Availability a WHERE a.doctor.id = :doctorId AND a.availableSlot = :availableSlot")
    void deleteByDoctorIdAndAvailableSlot(@Param("doctorId") Long doctorId,
                                          @Param("availableSlot") LocalDateTime availableSlot);


}
