package com.example.medical.repository;

import com.example.medical.entity.Doctor;
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
public class DoctorRepositoryTest {

    @Mock
    private DoctorRepository doctorRepository;

    private Doctor doctor;

    private User user;

    @BeforeEach
    void setUp(){
        user=new User();
        user.setId(1L);
        user.setFullname("Test");
        user.setEmail("test@gmail.com");
        user.setPassword("password@123");
        user.setRole("USER");

        doctor=new Doctor();
        doctor.setId(1L);
        doctor.setContactNumber("88259869123");
        doctor.setSpecialization("Dermatology");
        doctor.setUser(user);
    }

    @Test
    void findDoctorByID_WhenDoctorExists(){
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));

        Optional<Doctor> foundDoctor=doctorRepository.findById(1L);

        assertThat(foundDoctor).isPresent();
        assertThat(foundDoctor.get().getUser().getId()).isEqualTo(doctor.getUser().getId());

        verify(doctorRepository,times(1)).findById(1L);
    }

    @Test
    void findDoctorByID_WhenDoctorDoesNotExists(){
        when(doctorRepository.findById(2L)).thenReturn(Optional.empty());

        Optional<Doctor> foundDoctor=doctorRepository.findById(2L);

        assertThat(foundDoctor).isEmpty();

        verify(doctorRepository,times(1)).findById(2L);
    }

    @Test
    void findDoctorByUserID_WhenDoctorExists(){
        when(doctorRepository.findByUserId(1L)).thenReturn(Optional.of(doctor));

        Optional<Doctor> foundDoctor=doctorRepository.findByUserId(1L);

        assertThat(foundDoctor).isPresent();
        assertThat(foundDoctor.get().getUser().getId()).isEqualTo(doctor.getUser().getId());

        verify(doctorRepository,times(1)).findByUserId(1L);
    }

    @Test
    void findDoctorByUserID_WhenDoctorDoesNotExists(){
        when(doctorRepository.findByUserId(2L)).thenReturn(Optional.empty());

        Optional<Doctor> foundDoctor=doctorRepository.findByUserId(2L);

        assertThat(foundDoctor).isEmpty();

        verify(doctorRepository,times(1)).findByUserId(2L);
    }

    @Test
    void saveDoctor(){
        when(doctorRepository.saveAndFlush(doctor)).thenReturn(doctor);

        Doctor savedDoctor=doctorRepository.saveAndFlush(doctor);

        assertThat(savedDoctor).isNotNull();
        assertThat(savedDoctor.getUser().getEmail()).isEqualTo("test@gmail.com");

        verify(doctorRepository,times(1)).saveAndFlush(doctor);
    }

}
