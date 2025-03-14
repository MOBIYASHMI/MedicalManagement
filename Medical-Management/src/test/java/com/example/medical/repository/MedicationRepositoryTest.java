package com.example.medical.repository;

import com.example.medical.entity.Appointment;
import com.example.medical.entity.Medication;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MedicationRepositoryTest {

    @Mock
    private MedicationRepository medicationRepository;

    private Medication medication;

    private Appointment appointment;

    @BeforeEach
    void setUp(){
        appointment=new Appointment();
        appointment.setId(1L);

        medication=new Medication();
        medication.setAppointment(appointment);
        medication.setDosage("500mg");
        medication.setInstruction("Nothing");
        medication.setMedicineName("Paracetamol");
    }

    @Test
    void findByAppointmentId_WhenMedicationExists(){
        when(medicationRepository.findByAppointmentId(1L)).thenReturn(Optional.of(medication));

        Optional<Medication> foundMedication=medicationRepository.findByAppointmentId(1L);

        assertThat(foundMedication).isPresent();
        assertThat(foundMedication.get().getAppointment().getId()).isEqualTo(1L);

        verify(medicationRepository,times(1)).findByAppointmentId(1L);
    }

    @Test
    void findByAppointmentId_WhenMedicationDoesNotExists(){
        when(medicationRepository.findByAppointmentId(2L)).thenReturn(Optional.empty());

        Optional<Medication> foundMedication=medicationRepository.findByAppointmentId(2L);

        assertThat(foundMedication).isEmpty();

        verify(medicationRepository,times(1)).findByAppointmentId(2L);
    }

    @Test
    void saveMedication(){
        when(medicationRepository.save(medication)).thenReturn(medication);

        Medication savedMedication=medicationRepository.save(medication);

        assertThat(savedMedication).isNotNull();
        assertThat(savedMedication.getAppointment().getId()).isEqualTo(1L);

        verify(medicationRepository,times(1)).save(medication);
    }

    @Test
    void deleteMedication(){
        doNothing().when(medicationRepository).delete(medication);

        medicationRepository.delete(medication);

        verify(medicationRepository,times(1)).delete(medication);
    }
}
