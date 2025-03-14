package com.example.medical.repository;

import com.example.medical.entity.Appointment;
import com.example.medical.entity.Doctor;
import com.example.medical.entity.Patient;
import com.example.medical.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AppointmentRepositoryTest {
    @Mock
    private AppointmentRepository appointmentRepository;

    private Patient patient;

    private Doctor doctor;

    private User user;

    private Appointment appointment1;
    private Appointment appointment2;

    @BeforeEach
    void setUp(){
        user=new User();
        user.setId(1L);
        user.setEmail("test@gmail.com");

        patient=new Patient();
        patient.setId(1L);
        patient.setAge(20);
        patient.setGender("Female");
        patient.setMedicalHistory("No history");
        patient.setUser(user);

        doctor=new Doctor();
        doctor.setId(1L);
        doctor.setContactNumber("88259869123");
        doctor.setSpecialization("Dermatology");
        doctor.setUser(user);

    }

    @Test
    void testAppointments_FindByPatient_WhenExists(){

        appointment1=new Appointment();
        appointment1.setId(1L);
        appointment1.setMedicationAdded(true);
        appointment1.setDoctor(doctor);
        appointment1.setAppointmentTime(LocalDateTime.now().plusDays(1));
        appointment1.setPatient(patient);

        when(appointmentRepository.findByPatient(patient)).thenReturn(List.of(appointment1));

        List<Appointment> appointmentList=appointmentRepository.findByPatient(patient);

        assertThat(appointmentList).isNotNull();
        assertThat(appointmentList.size()).isEqualTo(1);

        verify(appointmentRepository,times(1)).findByPatient(patient);
    }

    @Test
    void testAppointments_FindByDoctor_WhenExists(){
        appointment2=new Appointment();
        appointment2.setId(2L);
        appointment2.setMedicationAdded(true);
        appointment2.setDoctor(doctor);
        appointment2.setAppointmentTime(LocalDateTime.now().plusDays(3));
        appointment2.setPatient(patient);

        when(appointmentRepository.findByDoctor(doctor)).thenReturn(List.of(appointment2));

        List<Appointment> appointmentList=appointmentRepository.findByDoctor(doctor);

        assertThat(appointmentList).isNotNull();
        assertThat(appointmentList.size()).isEqualTo(1);

        verify(appointmentRepository,times(1)).findByDoctor(doctor);
    }

    @Test
    void testSaveAppointment(){
        appointment1=new Appointment();
        appointment1.setId(1L);
        appointment1.setMedicationAdded(true);
        appointment1.setDoctor(doctor);
        appointment1.setAppointmentTime(LocalDateTime.now().plusDays(1));
        appointment1.setPatient(patient);

        when(appointmentRepository.save(appointment1)).thenReturn(appointment1);

        Appointment savedAppointment=appointmentRepository.save(appointment1);

        assertThat(savedAppointment).isNotNull();
        assertThat(savedAppointment.getDoctor().getSpecialization()).isEqualTo("Dermatology");

        verify(appointmentRepository,times(1)).save(appointment1);
    }

}
