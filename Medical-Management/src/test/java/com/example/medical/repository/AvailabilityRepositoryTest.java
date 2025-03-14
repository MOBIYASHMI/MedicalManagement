package com.example.medical.repository;

import com.example.medical.entity.Availability;
import com.example.medical.entity.Doctor;
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
public class AvailabilityRepositoryTest {

    @Mock
    private AvailabilityRepository availabilityRepository;

    private Doctor doctor;

    private Availability availability1;
    private Availability availability2;

    @BeforeEach
    void setUp(){

        doctor=new Doctor();
        doctor.setId(1L);
        doctor.setSpecialization("Dermatology");

        availability1 = new Availability();
        availability1.setId(1L);
        availability1.setDoctor(doctor);
        availability1.setAvailableSlot(LocalDateTime.now().plusDays(1));

        availability2 = new Availability();
        availability2.setId(2L);
        availability2.setDoctor(doctor);
        availability2.setAvailableSlot(LocalDateTime.now().plusDays(2));
    }

    @Test
    void FindListOfAvailability_ByDoctor(){
        when(availabilityRepository.findByDoctor(doctor)).thenReturn(List.of(availability1,availability2));

        List<Availability> availabilityList=availabilityRepository.findByDoctor(doctor);

        assertThat(availabilityList.size()).isEqualTo(2);
        assertThat(availability1.getDoctor().getId()).isEqualTo(1L);
        assertThat(availability2.getDoctor().getId()).isEqualTo(1L);

        verify(availabilityRepository,times(1)).findByDoctor(doctor);
    }

    @Test
    void testAvailability_ExistsByDoctorIdAndAvailableSlot(){
        when(availabilityRepository.existsByDoctorIdAndAvailableSlot(doctor.getId(), availability1.getAvailableSlot())).thenReturn(true);

        boolean availability= availabilityRepository.existsByDoctorIdAndAvailableSlot(doctor.getId(), availability1.getAvailableSlot());

        assertThat(availability).isTrue();

        verify(availabilityRepository,times(1)).existsByDoctorIdAndAvailableSlot(1L,availability1.getAvailableSlot());
    }

    @Test
    void testAvailability_DoesNotExistsByDoctorIdAndAvailableSlot(){
        when(availabilityRepository.existsByDoctorIdAndAvailableSlot(2L,availability2.getAvailableSlot())).thenReturn(false);

        boolean availability= availabilityRepository.existsByDoctorIdAndAvailableSlot(2L,availability2.getAvailableSlot());

        assertThat(availability).isFalse();
        verify(availabilityRepository,times(1)).existsByDoctorIdAndAvailableSlot(2L,availability2.getAvailableSlot());

    }

    @Test
    void testDeleteByDoctorId(){
        doNothing().when(availabilityRepository).deleteByDoctorId(1L);

        availabilityRepository.deleteByDoctorId(1L);

        verify(availabilityRepository,times(1)).deleteByDoctorId(1L);
    }

    @Test
    void testDeleteByDoctorIdAndAvailableSlots(){
        doNothing().when(availabilityRepository).deleteByDoctorIdAndAvailableSlot(1L,availability1.getAvailableSlot());

        availabilityRepository.deleteByDoctorIdAndAvailableSlot(doctor.getId(),availability1.getAvailableSlot());

        verify(availabilityRepository,times(1)).deleteByDoctorIdAndAvailableSlot(doctor.getId(),availability1.getAvailableSlot());
    }

    @Test
    void testSaveAvailability(){
        List<Availability> availabilityList=List.of(availability1,availability2);
        when(availabilityRepository.saveAll(availabilityList)).thenReturn(availabilityList);

        List<Availability> savedAvailabilities=availabilityRepository.saveAll(availabilityList);

        assertThat(savedAvailabilities).isNotNull();
        assertThat(savedAvailabilities.size()).isEqualTo(2);

        verify(availabilityRepository,times(1)).saveAll(availabilityList);
    }


}
