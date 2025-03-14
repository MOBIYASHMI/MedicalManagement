package com.example.medical.service;

import com.example.medical.dto.AppointmentDto;
import com.example.medical.dto.DoctorDto;
import com.example.medical.entity.Appointment;
import com.example.medical.entity.Availability;
import com.example.medical.entity.Doctor;
import com.example.medical.entity.User;
import com.example.medical.exceptions.DoctorNotFoundException;
import com.example.medical.repository.AppointmentRepository;
import com.example.medical.repository.AvailabilityRepository;
import com.example.medical.repository.DoctorRepository;
import com.example.medical.repository.UserRepository;
import com.example.medical.service.impl.DoctorServiceImpl;
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
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
public class DoctorServiceImplTest {

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AvailabilityRepository availabilityRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private DoctorServiceImpl doctorService;

    private User user;

    private Doctor doctor;

    private DoctorDto doctorDto;

    private Appointment appointment;

    private AppointmentDto appointmentDto;
    private Availability availability;

    @BeforeEach
    void setUp(){
        user=new User(2L,"test@gmail.com","Test","password123","Doctor");
        doctor=new Doctor(1L,"Dermatology","8978496215",user,null,null);
        doctorDto=new DoctorDto(1L,"Dermatology","8978496215",2L,null);

        appointment=new Appointment();
        appointment.setId(1L);
        appointment.setDoctor(doctor);
        appointment.setAppointmentTime(LocalDateTime.now());
        appointment.setStatus("Booked");
        appointment.setMedicationAdded(true);

        availability=new Availability();
        availability.setId(1L);
        availability.setAvailableSlot(LocalDateTime.now().plusDays(2));
        availability.setDoctor(doctor);

    }

    @Test
    void testSaveDoctorDetails_ExistingDoctor(){
        when(doctorRepository.findByUserId(doctorDto.getUserId())).thenReturn(Optional.of(doctor));
        when(doctorRepository.saveAndFlush(doctor)).thenReturn(doctor);
        when(modelMapper.map(doctor,DoctorDto.class)).thenReturn(doctorDto);

        DoctorDto savedDoctor=doctorService.saveDoctorDetails(doctorDto);

        assertThat(savedDoctor).isNotNull();
        assertThat(doctorDto.getUserId()).isEqualTo(savedDoctor.getUserId());

        verify(doctorRepository,times(1)).saveAndFlush(doctor);
    }

    @Test
    void testSaveDoctorDetails_NewDoctor(){
        when(doctorRepository.findByUserId(doctorDto.getUserId())).thenReturn(Optional.empty());
        when(userRepository.findById(2L)).thenReturn(Optional.of(user));
        when(doctorRepository.saveAndFlush(any(Doctor.class))).thenReturn(doctor);
        when(modelMapper.map(doctor,DoctorDto.class)).thenReturn(doctorDto);

        DoctorDto savedDoctor=doctorService.saveDoctorDetails(doctorDto);

        assertThat(savedDoctor).isNotNull();
        assertThat(doctorDto.getUserId()).isEqualTo(savedDoctor.getUserId());

        verify(doctorRepository,times(1)).saveAndFlush(any(Doctor.class));
    }

    @Test
    void testSaveDoctorDetails_UserNotFound(){
        when(doctorRepository.findByUserId(doctorDto.getUserId())).thenReturn(Optional.empty());
        when(userRepository.findById(doctorDto.getUserId())).thenReturn(Optional.empty());

        RuntimeException exception=assertThrows(RuntimeException.class,()-> doctorService.saveDoctorDetails(doctorDto));
        assertThat("User not found with ID: " + doctorDto.getUserId()).isEqualTo(exception.getMessage());
    }

    @Test
    void testViewAppointments(){
        when(doctorRepository.findById(doctor.getId())).thenReturn(Optional.of(doctor));
        when(appointmentRepository.findByDoctor(doctor)).thenReturn(List.of(appointment));
        when(modelMapper.map(appointment,AppointmentDto.class)).thenReturn(appointmentDto);

        List<AppointmentDto> appointments=doctorService.viewAppointments(doctor.getId());

        assertThat(appointments).isNotNull();
        assertThat(appointments.size()).isEqualTo(1);
        assertThat(doctor.getId()).isEqualTo(appointment.getDoctor().getId());

        verify(appointmentRepository,times(1)).findByDoctor(doctor);
    }

    @Test
    void testViewAppointments_DoctorNotFound(){
        Long doctorId=2L;
        when(doctorRepository.findById(doctorId)).thenReturn(Optional.empty());

        DoctorNotFoundException exception=assertThrows(DoctorNotFoundException.class,()->doctorService.viewAppointments(doctorId));
        assertThat("Doctor with ID " + doctorId + " not found").isEqualTo(exception.getMessage());

        verify(appointmentRepository,never()).findByDoctor(doctor);
    }

    @Test
    void TestUpdateAvailability(){
        List<LocalDateTime> availableSlots=List.of(LocalDateTime.now(),LocalDateTime.now().plusHours(2));
        List<Availability> availabilityList = availableSlots.stream()
                .map(slot -> new Availability(slot, doctor))
                .collect(Collectors.toList());
        when(doctorRepository.findById(doctor.getId())).thenReturn(Optional.of(doctor));
        doNothing().when(availabilityRepository).deleteByDoctorId(doctor.getId());
        when(availabilityRepository.saveAll(anyList())).thenReturn(anyList());

        doctorService.updateAvailability(doctor.getId(),availableSlots);

        verify(doctorRepository, times(1)).findById(doctor.getId());
        verify(availabilityRepository, times(1)).deleteByDoctorId(doctor.getId());
        verify(availabilityRepository, times(1)).saveAll(anyList());
    }

    @Test
    void TestUpdateAvailability_DoctorNotFound(){
        Long doctorId=2L;
        when(doctorRepository.findById(doctorId)).thenReturn(Optional.empty());

        DoctorNotFoundException exception=assertThrows(DoctorNotFoundException.class,()->doctorService.viewAppointments(doctorId));
        assertThat("Doctor with ID " + doctorId + " not found").isEqualTo(exception.getMessage());

        verify(appointmentRepository,never()).findByDoctor(doctor);
    }

    @Test
    void testGetDoctorDetails(){
        when(doctorRepository.findByUserId(doctor.getUser().getId())).thenReturn(Optional.of(doctor));
        when(modelMapper.map(doctor,DoctorDto.class)).thenReturn(doctorDto);

        DoctorDto foundDoctor=doctorService.getDoctorDetails(doctor.getUser().getId());

        assertThat(foundDoctor).isNotNull();
        assertThat(doctor.getUser().getId()).isEqualTo(foundDoctor.getUserId());
    }

}
