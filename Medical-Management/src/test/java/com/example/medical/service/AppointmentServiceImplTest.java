package com.example.medical.service;

import com.example.medical.dto.AppointmentDto;
import com.example.medical.entity.Appointment;
import com.example.medical.entity.Doctor;
import com.example.medical.entity.Patient;
import com.example.medical.entity.User;
import com.example.medical.exceptions.AppointmentNotFoundException;
import com.example.medical.exceptions.DoctorNotFoundException;
import com.example.medical.exceptions.PatientNotFoundException;
import com.example.medical.repository.AppointmentRepository;
import com.example.medical.repository.AvailabilityRepository;
import com.example.medical.repository.DoctorRepository;
import com.example.medical.repository.PatientRepository;
import com.example.medical.service.impl.AppointmentServiceImpl;
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

@ExtendWith(MockitoExtension.class)
@SpringBootTest
public class AppointmentServiceImplTest {
    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private PatientRepository patientRepository;

    @Mock
    AvailabilityRepository availabilityRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private AppointmentServiceImpl appointmentService;

    private User user;

    private Doctor doctor;

    private Appointment appointment;
    private AppointmentDto appointmentDto;
    private Patient patient;

    @BeforeEach
    void setUp(){
        user=new User(2L,"test@gmail.com","Test","password123","Doctor");

        doctor=new Doctor(1L,"Dermatology","8978496215",user,null,null);

        patient=new Patient(1L,27,"Female","No history",user,null);

        appointment=new Appointment(1L,doctor,patient,LocalDateTime.now(),"Booked",false,null);

        appointmentDto=new AppointmentDto(1L,1L,1L,LocalDateTime.now(),"Booked",null,doctor.getUser().getFullname(),doctor.getSpecialization(),patient.getUser().getFullname(),false);
    }

    @Test
    void testSaveAppointment(){
        when(doctorRepository.findById(appointmentDto.getDoctorId())).thenReturn(Optional.of(doctor));
        when(patientRepository.findById(appointmentDto.getPatientId())).thenReturn(Optional.of(patient));
        when(modelMapper.map(appointmentDto,Appointment.class)).thenReturn(appointment);
        when(appointmentRepository.save(appointment)).thenReturn(appointment);
        when(modelMapper.map(appointment,AppointmentDto.class)).thenReturn(appointmentDto);

        AppointmentDto savedAppointment=appointmentService.saveAppointment(appointmentDto);

        assertThat(savedAppointment).isNotNull();
        assertThat(appointmentDto.getPatientId()).isEqualTo(savedAppointment.getPatientId());
        assertThat(appointmentDto.getDoctorId()).isEqualTo(savedAppointment.getDoctorId());

        verify(doctorRepository, times(1)).findById(appointmentDto.getDoctorId());
        verify(patientRepository, times(1)).findById(appointmentDto.getPatientId());
        verify(appointmentRepository, times(1)).save(appointment);
    }

    @Test
    void testSaveAppointment_DoctorNotFound(){
        when(doctorRepository.findById(appointmentDto.getDoctorId())).thenReturn(Optional.empty());

        DoctorNotFoundException exception=assertThrows(DoctorNotFoundException.class,()->appointmentService.saveAppointment(appointmentDto));
        assertThat("Doctor not found with ID: " + appointmentDto.getDoctorId()).isEqualTo(exception.getMessage());

        verify(doctorRepository, times(1)).findById(appointmentDto.getDoctorId());
        verify(appointmentRepository,never()).save(appointment);
    }

    @Test
    void testSaveAppointment_PatientNoFound(){
        Long patientId=2L;
        appointmentDto=new AppointmentDto();
        appointmentDto.setPatientId(patientId);
        when(doctorRepository.findById(appointmentDto.getDoctorId())).thenReturn(Optional.of(doctor));
        when(patientRepository.findById(patientId)).thenReturn(Optional.empty());

        PatientNotFoundException exception=assertThrows(PatientNotFoundException.class,()->appointmentService.saveAppointment(appointmentDto));
        assertThat("Patient not found with ID: " + patientId).isEqualTo(exception.getMessage());

        verify(doctorRepository, times(1)).findById(appointmentDto.getDoctorId());
        verify(patientRepository, times(1)).findById(patientId);
        verify(appointmentRepository,never()).save(appointment);
    }

    @Test
    void testDeleteAvailability(){
        doNothing().when(availabilityRepository).deleteByDoctorIdAndAvailableSlot(doctor.getId(),appointment.getAppointmentTime());
        availabilityRepository.deleteByDoctorIdAndAvailableSlot(doctor.getId(),appointment.getAppointmentTime());
    }

    @Test
    void testGetAppointmentsByDoctor(){
        Appointment appointment1 = new Appointment(1L,doctor,patient,LocalDateTime.now(),"Booked",false,null);
        Appointment appointment2 = new Appointment(2L,doctor,patient,LocalDateTime.now().plusHours(2),"Booked",false,null);
        List<Appointment> appointmentList=List.of(appointment1,appointment2);
        when(doctorRepository.findById(doctor.getId())).thenReturn(Optional.of(doctor));
        when(appointmentRepository.findByDoctor(doctor)).thenReturn(appointmentList);

        List<AppointmentDto> appointments=appointmentService.getAppointmentsByDoctor(doctor.getId());

        assertThat(appointments).isNotNull();
        assertThat(appointments.size()).isEqualTo(2);

        verify(doctorRepository,times(1)).findById(doctor.getId());
        verify(appointmentRepository,times(1)).findByDoctor(doctor);
    }

    @Test
    void testGetAppointmentsByDoctor_DoctorNotFound(){
        when(doctorRepository.findById(appointmentDto.getDoctorId())).thenReturn(Optional.empty());

        DoctorNotFoundException exception=assertThrows(DoctorNotFoundException.class,()->appointmentService.getAppointmentsByDoctor(doctor.getId()));
        assertThat("Doctor not found with ID: " + appointmentDto.getDoctorId()).isEqualTo(exception.getMessage());

        verify(appointmentRepository,never()).findByDoctor(doctor);
    }

    @Test
    void testGetAppointmentsByPatient(){
        Appointment appointment1 = new Appointment(1L,doctor,patient,LocalDateTime.now(),"Booked",false,null);
        Appointment appointment2 = new Appointment(2L,doctor,patient,LocalDateTime.now().plusHours(2),"Booked",false,null);
        List<Appointment> appointmentList=List.of(appointment1,appointment2);
        when(patientRepository.findById(patient.getId())).thenReturn(Optional.of(patient));
        when(appointmentRepository.findByPatient(patient)).thenReturn(appointmentList);

        List<AppointmentDto> appointments=appointmentService.getAppointmentsByPatient(patient.getId());

        assertThat(appointments).isNotNull();
        assertThat(appointments.size()).isEqualTo(2);

        verify(patientRepository,times(1)).findById(patient.getId());
        verify(appointmentRepository,times(1)).findByPatient(patient);
    }

    @Test
    void testGetAppointmentsByPatient_PatientNotFound(){
        when(patientRepository.findById(appointmentDto.getPatientId())).thenReturn(Optional.empty());

        PatientNotFoundException exception=assertThrows(PatientNotFoundException.class,()->appointmentService.getAppointmentsByPatient(patient.getId()));
        assertThat("Patient not found with ID: " + appointmentDto.getPatientId()).isEqualTo(exception.getMessage());

        verify(appointmentRepository,never()).findByPatient(patient);
    }

    @Test
    void testCancelAppointment(){
        when(appointmentRepository.findById(appointment.getId())).thenReturn(Optional.of(appointment));
        doNothing().when(appointmentRepository).delete(appointment);

        appointmentService.cancelAppointment(appointment.getId());

        verify(appointmentRepository,times(1)).delete(appointment);
    }

    @Test
    void testCancelAppointment_AppointmentNotFound(){
        Long appointmentId=3L;
        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.empty());

        AppointmentNotFoundException exception=assertThrows(AppointmentNotFoundException.class,()->appointmentService.cancelAppointment(appointmentId));
        assertThat("Appointment not found with ID: " + appointmentId).isEqualTo(exception.getMessage());

        verify(appointmentRepository,never()).delete(appointment);
    }

    @Test
    void testGetAppointmentDetails(){
        when(appointmentRepository.findById(appointment.getId())).thenReturn(Optional.of(appointment));
        when(modelMapper.map(appointment,AppointmentDto.class)).thenReturn(appointmentDto);

        AppointmentDto foundAppointment=appointmentService.getAppointmentDetails(appointment.getId());

        assertThat(foundAppointment).isNotNull();

        verify(appointmentRepository,times(1)).findById(appointment.getId());
    }

    @Test
    void testGetAppointmentDetails_AppointmentNotFound(){
        Long appointmentId=3L;
        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.empty());

        AppointmentNotFoundException exception=assertThrows(AppointmentNotFoundException.class,()->appointmentService.getAppointmentDetails(appointmentId));
        assertThat("Appointment not found with ID: " + appointmentId).isEqualTo(exception.getMessage());

        verify(appointmentRepository,times(1)).findById(appointmentId);
    }
}
