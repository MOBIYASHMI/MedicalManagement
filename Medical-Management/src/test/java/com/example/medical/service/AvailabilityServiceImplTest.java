package com.example.medical.service;

import com.example.medical.dto.AvailabilityDto;
import com.example.medical.entity.Availability;
import com.example.medical.entity.Doctor;
import com.example.medical.entity.User;
import com.example.medical.exceptions.DoctorNotFoundException;
import com.example.medical.repository.AvailabilityRepository;
import com.example.medical.repository.DoctorRepository;
import com.example.medical.service.impl.AvailabilityServiceImpl;
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
public class AvailabilityServiceImplTest {
    @Mock
    private AvailabilityRepository availabilityRepository;

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private AvailabilityServiceImpl availabilityService;

    private User user;
    private Doctor doctor;
    private Availability availability1;
    private Availability availability2;
    private AvailabilityDto availabilityDto1;
    private AvailabilityDto availabilityDto2;
    @BeforeEach
    void setUp(){
        user=new User(2L,"test@gmail.com","Test","password123","Doctor");
        doctor=new Doctor(1L,"Dermatology","8978496215",user,null,null);

        availability1 = new Availability();
        availability1.setId(1L);
        availability1.setDoctor(doctor);
        availability1.setAvailableSlot(LocalDateTime.now().plusDays(1));

        availability2 = new Availability();
        availability2.setId(2L);
        availability2.setDoctor(doctor);
        availability2.setAvailableSlot(LocalDateTime.now().plusDays(2));

        availabilityDto1=new AvailabilityDto(1L,LocalDateTime.now().plusDays(1),1L);
        availabilityDto2=new AvailabilityDto(2L,LocalDateTime.now().plusDays(2),1L);
    }

    @Test
    void testAddAvailability(){
        List<AvailabilityDto> availabilitySlots=List.of(availabilityDto1,availabilityDto2);

        Availability availability1 = new Availability(availabilityDto1.getAvailableSlot(), doctor);
        Availability availability2 = new Availability(availabilityDto2.getAvailableSlot(), doctor);

        List<Availability> availabilityList=List.of(availability1,availability2);

        when(doctorRepository.findById(doctor.getId())).thenReturn(Optional.of(doctor));
        when(modelMapper.map(availabilityDto1, Availability.class)).thenReturn(availability1);
        when(modelMapper.map(availabilityDto2, Availability.class)).thenReturn(availability2);
        when(availabilityRepository.saveAll(availabilityList)).thenReturn(availabilityList);

        availabilityService.addAvailability(doctor.getId(),availabilitySlots);

        verify(doctorRepository, times(1)).findById(doctor.getId());
        verify(modelMapper, times(2)).map(any(AvailabilityDto.class), eq(Availability.class));
        verify(availabilityRepository, times(1)).saveAll(availabilityList);
    }

    @Test
    void testAddAvailability_DoctorNotFound(){
        Long doctorId=2L;
        List<AvailabilityDto> availabilitySlots=List.of(availabilityDto1,availabilityDto2);
        List<Availability> availabilityList=List.of(availability1,availability2);
        when(doctorRepository.findById(doctorId)).thenReturn(Optional.empty());

        DoctorNotFoundException exception=assertThrows(DoctorNotFoundException.class,()->availabilityService.addAvailability(doctorId,availabilitySlots));
        assertThat("Doctor not found with ID: " + doctorId).isEqualTo(exception.getMessage());

        verify(availabilityRepository,never()).saveAll(availabilityList);
    }

    @Test
    void testGetAvailabilityByDoctor(){
        List<AvailabilityDto> availabilitySlots=List.of(availabilityDto1,availabilityDto2);
        List<Availability> availabilityList=List.of(availability1,availability2);

        when(doctorRepository.findById(doctor.getId())).thenReturn(Optional.of(doctor));
        when(availabilityRepository.findByDoctor(doctor)).thenReturn(availabilityList);
        when(modelMapper.map(availability1, AvailabilityDto.class)).thenReturn(availabilityDto1);
        when(modelMapper.map(availability2, AvailabilityDto.class)).thenReturn(availabilityDto2);

        List<AvailabilityDto> availabilityDtoList=availabilityService.getAvailabilityByDoctor(doctor.getId());

        assertThat(availabilityDtoList).isNotNull();
        assertThat(availabilityDtoList.size()).isEqualTo(2);

    }

    @Test
    void testGetAvailabilityByDoctor_DoctorNotFound(){
        Long doctorId=2L;
        when(doctorRepository.findById(doctorId)).thenReturn(Optional.empty());

        DoctorNotFoundException exception=assertThrows(DoctorNotFoundException.class,()->availabilityService.getAvailabilityByDoctor(doctorId));
        assertThat("Doctor not found with ID: " + doctorId).isEqualTo(exception.getMessage());

        verify(availabilityRepository,never()).findByDoctor(doctor);
    }
}
