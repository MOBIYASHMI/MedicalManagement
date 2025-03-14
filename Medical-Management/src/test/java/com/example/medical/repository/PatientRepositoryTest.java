package com.example.medical.repository;

import com.example.medical.entity.Patient;
import com.example.medical.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PatientRepositoryTest {

    @Mock
    private PatientRepository patientRepository;

    private User user;

    private Patient patient;

    @BeforeEach
    void setUp(){
        user=new User();
        user.setId(1L);
        user.setFullname("Test");
        user.setEmail("test@gmail.com");
        user.setPassword("password@123");
        user.setRole("USER");

        patient=new Patient();
        patient.setId(1L);
        patient.setAge(23);
        patient.setGender("Male");
        patient.setMedicalHistory("NIL");
        patient.setUser(user);
    }

    @Test
    void findPatientByUserId_WhenPatientExists(){
        when(patientRepository.findByUserId(1L)).thenReturn(Optional.of(patient));

        Optional<Patient> foundPatient=patientRepository.findByUserId(1L);

        assertThat(foundPatient).isPresent();
        assertThat(foundPatient.get().getUser().getEmail()).isEqualTo("test@gmail.com");

        verify(patientRepository,times(1)).findByUserId(1L);
    }

    @Test
    void findPatientByUserId_WhenPatientDoesNotExists(){
        when(patientRepository.findByUserId(2L)).thenReturn(Optional.empty());

        Optional<Patient> foundPatient=patientRepository.findByUserId(2L);

        assertThat(foundPatient).isEmpty();

        verify(patientRepository,times(1)).findByUserId(2L);
    }

    @Test
    void savePatientDetails(){
        when(patientRepository.save(patient)).thenReturn(patient);

        Patient savedPatient=patientRepository.save(patient);

        assertThat(savedPatient).isNotNull();
        assertThat(savedPatient.getUser().getEmail()).isEqualTo("test@gmail.com");

        verify(patientRepository,times(1)).save(patient);
    }

}
