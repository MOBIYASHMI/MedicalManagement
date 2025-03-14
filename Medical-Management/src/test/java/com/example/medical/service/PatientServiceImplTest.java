package com.example.medical.service;

import com.example.medical.dto.PatientDto;
import com.example.medical.entity.Patient;
import com.example.medical.entity.User;
import com.example.medical.repository.PatientRepository;
import com.example.medical.repository.UserRepository;
import com.example.medical.service.impl.PatientServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
public class PatientServiceImplTest {

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private PatientServiceImpl patientService;

    private PatientDto patientDto;

    private Patient patient;

    private User user;

    @BeforeEach
    void setUp(){
        user=new User();
        user.setId(2L);
        user.setEmail("test@gmail.com");
        user.setFullname("Test");
        user.setRole("Patient");

        patient=new Patient();
        patient.setId(1L);
        patient.setAge(23);
        patient.setGender("Female");
        patient.setMedicalHistory("No history");
        patient.setUser(user);

        patientDto = new PatientDto(1L, 23, "Female","No history", 2L);

    }

    @Test
    void testSavePatientDetails_ExistingPatient(){
        when(patientRepository.findByUserId(2L)).thenReturn(Optional.of(patient));
        when(patientRepository.saveAndFlush(patient)).thenReturn(patient);
        when(modelMapper.map(patient,PatientDto.class)).thenReturn(patientDto);

        PatientDto savedPatient=patientService.savePatientDetails(patientDto);

        assertThat(savedPatient).isNotNull();
        assertThat(patientDto.getUserId()).isEqualTo(savedPatient.getUserId());

        verify(patientRepository,times(1)).saveAndFlush(any(Patient.class));
    }

    @Test
    void testSavePatientDetails_NewPatient(){
        when(patientRepository.findByUserId(2L)).thenReturn(Optional.empty());
        when(modelMapper.map(patientDto,Patient.class)).thenReturn(patient);
        when(userRepository.findById(2L)).thenReturn(Optional.of(user));
        when(patientRepository.saveAndFlush(patient)).thenReturn(patient);
        when(modelMapper.map(patient,PatientDto.class)).thenReturn(patientDto);

        PatientDto savedPatient=patientService.savePatientDetails(patientDto);

        assertThat(savedPatient).isNotNull();
        assertThat(patientDto.getUserId()).isEqualTo(savedPatient.getUserId());

        verify(patientRepository,times(1)).saveAndFlush(any(Patient.class));
    }

    @Test
    void testSavePatientDetails_UserNotFound(){
        when(patientRepository.findByUserId(patientDto.getUserId())).thenReturn(Optional.empty());
        when(userRepository.findById(patientDto.getUserId())).thenReturn(Optional.empty());

        RuntimeException exception=assertThrows(RuntimeException.class, ()-> patientService.savePatientDetails(patientDto));
        assertThat("User not found with ID: " + patientDto.getUserId()).isEqualTo(exception.getMessage());
    }

    @Test
    void testGetPatientDetails_WhenPatientExists(){
        when(patientRepository.findByUserId(patientDto.getUserId())).thenReturn(Optional.of(patient));
        when(modelMapper.map(patient,PatientDto.class)).thenReturn(patientDto);

        PatientDto foundPatient=patientService.getPatientDetails(patientDto.getUserId());

        assertThat(foundPatient).isNotNull();
        assertThat(patientDto.getUserId()).isEqualTo(foundPatient.getUserId());

        verify(patientRepository,times(1)).findByUserId(patientDto.getUserId());
    }

    @Test
    void testGetPatientDetails_WhenPatientDoesNotExists(){
        when(patientRepository.findByUserId(patientDto.getUserId())).thenReturn(Optional.empty());

        PatientDto foundPatient=patientService.getPatientDetails(patientDto.getUserId());

        assertThat(foundPatient).isNull();

        verify(patientRepository,times(1)).findByUserId(patientDto.getUserId());
    }

}
