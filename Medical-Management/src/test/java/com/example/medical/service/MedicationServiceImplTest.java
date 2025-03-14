package com.example.medical.service;

import com.example.medical.dto.MedicationDto;
import com.example.medical.entity.*;
import com.example.medical.exceptions.AppointmentNotFoundException;
import com.example.medical.exceptions.DoctorNotFoundException;
import com.example.medical.exceptions.MedicationNotFoundException;
import com.example.medical.repository.AppointmentRepository;
import com.example.medical.repository.DoctorRepository;
import com.example.medical.repository.MedicationRepository;
import com.example.medical.service.impl.MedicationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.springframework.test.util.AssertionErrors.assertFalse;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
public class MedicationServiceImplTest {

    @Mock
    private MedicationRepository medicationRepository;

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private MedicationServiceImpl medicationService;

    private User user;
    private Doctor doctor;
    private Patient patient;
    private Appointment appointment;
    private Medication medication;
    private MedicationDto medicationDto;

    @BeforeEach
    void setUp(){
        user=new User(2L,"test@gmail.com","Test","password123","Doctor");

        doctor=new Doctor(1L,"Dermatology","8978496215",user,null,null);

        patient=new Patient(1L,27,"Female","No history",user,null);

        appointment=new Appointment(1L,doctor,patient, LocalDateTime.now(),"Booked",false,null);

        medication=new Medication(1L,"Paracetamol","500 mg","Nothing",appointment);
        medicationDto=new MedicationDto(1L,"Paracetamol","500 mg","Nothing",1L);
    }

    @Test
    void testAddMedication(){
        when(appointmentRepository.findById(medicationDto.getAppointmentId())).thenReturn(Optional.of(appointment));
        when(modelMapper.map(medicationDto,Medication.class)).thenReturn(medication);
        when(medicationRepository.save(medication)).thenReturn(medication);
        when(modelMapper.map(medication,MedicationDto.class)).thenReturn(medicationDto);

        MedicationDto savedMedication=medicationService.addMedication(medicationDto);
        assertThat(savedMedication).isNotNull();
        assertThat(savedMedication.getAppointmentId()).isEqualTo(medicationDto.getAppointmentId());

        verify(appointmentRepository,times(1)).findById(medicationDto.getAppointmentId());
        verify(medicationRepository,times(1)).save(medication);
        verify(appointmentRepository,times(1)).save(appointment);
        verify(modelMapper,times(2)).map(any(),any());
    }

    @Test
    void testAddMedication_AppointmentNotFound(){
        Long appointmentId=2L;
        medicationDto=new MedicationDto(1L,"Paracetamol","500 mg","Nothing",2L);

        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.empty());

        AppointmentNotFoundException exception=assertThrows(AppointmentNotFoundException.class,()->medicationService.addMedication(medicationDto));
        assertThat("Appointment not found with ID: " + appointmentId).isEqualTo(exception.getMessage());

        verify(appointmentRepository,times(1)).findById(appointmentId);
        verify(medicationRepository,never()).save(medication);
        verify(appointmentRepository,never()).save(appointment);
        verify(modelMapper,never()).map(any(),any());
    }

    @Test
    void testGetMedicationsByAppointment(){
        when(medicationRepository.findByAppointmentId(medicationDto.getAppointmentId())).thenReturn(Optional.of(medication));
        when(modelMapper.map(medication,MedicationDto.class)).thenReturn(medicationDto);

        MedicationDto foundMedication=medicationService.getMedicationsByAppointment(medicationDto.getAppointmentId());

        assertThat(foundMedication).isNotNull();
        assertThat(foundMedication.getAppointmentId()).isEqualTo(medicationDto.getAppointmentId());

        verify(medicationRepository,times(1)).findByAppointmentId(medicationDto.getAppointmentId());
        verify(modelMapper,times(1)).map(medication,MedicationDto.class);
    }

    @Test
    void testGetMedicationsByAppointment_MedicationNotFound(){
        Long appointmentId=2L;
        when(medicationRepository.findByAppointmentId(appointmentId)).thenReturn(Optional.empty());

        MedicationDto foundMedication=medicationService.getMedicationsByAppointment(appointmentId);

        assertThat(foundMedication).isNull();

        verify(medicationRepository,times(1)).findByAppointmentId(appointmentId);
        verify(modelMapper,never()).map(medication,MedicationDto.class);
    }

    @Test
    void testUpdateMedication(){
        when(medicationRepository.findByAppointmentId(medicationDto.getAppointmentId())).thenReturn(Optional.of(medication));
        medicationService.updateMedication(medicationDto);

        assertThat(medication.getAppointment().getId()).isEqualTo(medicationDto.getAppointmentId());
        assertThat(medication.getDosage()).isEqualTo(medicationDto.getDosage());
        assertThat(medication.getMedicineName()).isEqualTo(medicationDto.getMedicineName());
        assertThat(medication.getInstruction()).isEqualTo(medicationDto.getInstruction());


        verify(medicationRepository,times(1)).findByAppointmentId(medicationDto.getAppointmentId());
        verify(medicationRepository,times(1)).save(medication);
    }

    @Test
    void testUpdateMedication_MedicationNotFound(){
        Long appointmentId=2L;
        MedicationDto newMedicationDto=new MedicationDto();
        newMedicationDto.setAppointmentId(2L);
        when(medicationRepository.findByAppointmentId(appointmentId)).thenReturn(Optional.empty());

        MedicationNotFoundException exception=assertThrows(MedicationNotFoundException.class,()->medicationService.updateMedication(newMedicationDto));

        assertThat(exception.getMessage()).isEqualTo("Medication not found for this ID: " + appointmentId);

        verify(medicationRepository, times(1)).findByAppointmentId(appointmentId);
        verify(medicationRepository, never()).save(any());
    }

    @Test
    void testDeleteMedication(){
        when(medicationRepository.findByAppointmentId(medicationDto.getAppointmentId())).thenReturn(Optional.of(medication));
        when(appointmentRepository.findById(medicationDto.getAppointmentId())).thenReturn(Optional.of(appointment));

        medicationService.deleteMedication(medicationDto.getAppointmentId());

        assertFalse("MedicationAdded should be set to false after deletion",appointment.isMedicationAdded());

        verify(medicationRepository,times(1)).findByAppointmentId(medicationDto.getAppointmentId());
        verify(medicationRepository,times(1)).delete(medication);

        verify(appointmentRepository,times(1)).findById(medicationDto.getAppointmentId());
        verify(appointmentRepository,times(1)).save(appointment);
    }

    @Test
    void testDeleteMedication_MedicationNotFound(){
        Long appointmentId=2L;
        MedicationDto newMedicationDto=new MedicationDto();
        newMedicationDto.setAppointmentId(2L);
        when(medicationRepository.findByAppointmentId(appointmentId)).thenReturn(Optional.empty());

        MedicationNotFoundException exception=assertThrows(MedicationNotFoundException.class,()->medicationService.deleteMedication(appointmentId));

        assertThat(exception.getMessage()).isEqualTo("Medication not found with Appointment ID: " + appointmentId);

        verify(medicationRepository, times(1)).findByAppointmentId(appointmentId);
        verify(appointmentRepository,never()).findById(appointmentId);
        verify(medicationRepository, never()).delete(medication);
        verify(appointmentRepository,never()).save(appointment);
    }

    @Test
    void testDeleteMedication_AppointmentNotFound(){
        Long appointmentId=2L;
        when(medicationRepository.findByAppointmentId(appointmentId)).thenReturn(Optional.of(medication));
        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.empty());

        AppointmentNotFoundException exception=assertThrows(AppointmentNotFoundException.class,()->medicationService.deleteMedication(appointmentId));

        assertThat(exception.getMessage()).isEqualTo("Appointment not found with ID: "+appointmentId);

        verify(medicationRepository,times(1)).findByAppointmentId(appointmentId);
        verify(appointmentRepository,times(1)).findById(appointmentId);
        verify(medicationRepository, never()).delete(medication);
        verify(appointmentRepository,never()).save(appointment);
    }

    @Test
    void testViewPrescribedMedications(){
        Appointment appointment1 = new Appointment();
        appointment1.setId(100L);

        Appointment appointment2 = new Appointment();
        appointment2.setId(101L);

        Medication medication1 = new Medication();
        medication1.setId(1L);
        medication1.setMedicineName("Paracetamol");

        Medication medication2 = new Medication();
        medication2.setId(2L);
        medication2.setMedicineName("Ibuprofen");

        appointment1.setMedications(List.of(medication1));
        appointment2.setMedications(List.of(medication2));

        doctor.setAppointments(List.of(appointment1, appointment2));

        MedicationDto medicationDto1 = new MedicationDto();
        medicationDto1.setId(1L);
        medicationDto1.setMedicineName("Paracetamol");

        MedicationDto medicationDto2 = new MedicationDto();
        medicationDto2.setId(2L);
        medicationDto2.setMedicineName("Ibuprofen");

        when(doctorRepository.findById(doctor.getId())).thenReturn(Optional.of(doctor));
        when(modelMapper.map(medication1,MedicationDto.class)).thenReturn(medicationDto1);
        when(modelMapper.map(medication2,MedicationDto.class)).thenReturn(medicationDto2);

        List<MedicationDto> foundMedication=medicationService.viewPrescribedMedications(doctor.getId());

        assertThat(foundMedication).isNotNull();
        assertThat(foundMedication.size()).isEqualTo(2);

        verify(doctorRepository, times(1)).findById(doctor.getId());
        verify(modelMapper, times(1)).map(medication1, MedicationDto.class);
        verify(modelMapper, times(1)).map(medication2, MedicationDto.class);
    }

    @Test
    void testViewPrescribedMedications_DoctorNotFound(){
        Long doctorId=2L;
        when(doctorRepository.findById(doctorId)).thenReturn(Optional.empty());

        DoctorNotFoundException exception=assertThrows(DoctorNotFoundException.class,()->medicationService.viewPrescribedMedications(doctorId));

        assertThat("Doctor not found with ID: " + doctorId).isEqualTo(exception.getMessage());

        verify(doctorRepository,times(1)).findById(doctorId);
    }
}
