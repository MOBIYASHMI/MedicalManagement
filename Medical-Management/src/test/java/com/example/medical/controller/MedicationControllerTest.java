package com.example.medical.controller;

import com.example.medical.dto.MedicationDto;
import com.example.medical.entity.Doctor;
import com.example.medical.entity.Patient;
import com.example.medical.entity.User;
import com.example.medical.repository.DoctorRepository;
import com.example.medical.repository.PatientRepository;
import com.example.medical.repository.UserRepository;
import com.example.medical.service.AppointmentService;
import com.example.medical.service.MedicationService;
import com.example.medical.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.Model;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(MedicationController.class)
public class MedicationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MedicationService medicationService;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private AppointmentService appointmentService;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private DoctorRepository doctorRepository;

    @MockitoBean
    private PatientRepository patientRepository;

    @MockitoBean
    private Model model;

    @InjectMocks
    private MedicationController medicationController;

    @BeforeEach
    void setUp(){
        mockMvc= MockMvcBuilders.standaloneSetup(medicationController).build();
    }

    @Test
    void testShowMedication() throws Exception{
        List<String> predefinedMedicines=List.of("Paracetamol", "Ibuprofen", "Amoxicillin", "Cetirizine", "Aspirin");

        mockMvc.perform(get("/medications/add")
                        .param("appointmentId","1"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("medicineList"))
                .andExpect(model().attributeExists("medication"))
                .andExpect(model().attributeExists("appointmentId"))
                .andExpect(view().name("medication-form"));
    }

    @Test
    void testAddMedication_ExistingMedication() throws Exception{
        MedicationDto medicationDto=new MedicationDto(1L,"Paracetamol","500mg","Nothing",1L);

        when(medicationService.getMedicationsByAppointment(1L)).thenReturn(medicationDto);
        doNothing().when(medicationService).updateMedication(medicationDto);

        mockMvc.perform(post("/medications/add")
                .param("appointmentId","1")
                .param("selectedMedicines","Paracetamol")
                .param("dosage","500mg")
                .param("instruction","Nothing"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/medications/view"));
    }

    @Test
    void testAddMedication_NewMedication() throws Exception{
        MedicationDto medicationDto=new MedicationDto(1L,"Paracetamol","500mg","Nothing",1L);

        when(medicationService.getMedicationsByAppointment(1L)).thenReturn(medicationDto);
        when(medicationService.addMedication(medicationDto)).thenReturn(medicationDto);

        mockMvc.perform(post("/medications/add")
                        .param("appointmentId","1")
                        .param("selectedMedicines","Paracetamol")
                        .param("dosage","500mg")
                        .param("instruction","Nothing"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/medications/view"));
    }

    @Test
    void testViewMedications_AsDoctor() throws Exception{
        User user=new User(1L,"doctor@gmail.com","test","password","DOCTOR");
        Doctor doctor=new Doctor(1L,"Dermatology","8758236587",user,null,null);
        MedicationDto medicationDto=new MedicationDto(1L,"Paracetamol","500mg","Nothing",1L);

        Principal mockPrincipal=mock(Principal.class);
        when(mockPrincipal.getName()).thenReturn("doctor@gmail.com");

        when(userRepository.findByEmail("doctor@gmail.com")).thenReturn(Optional.of(user));
        when(medicationService.getMedicationsByAppointment(1L)).thenReturn(medicationDto);
        when(doctorRepository.findByUserId(1L)).thenReturn(Optional.of(doctor));

        mockMvc.perform(get("/medications/view")
                        .principal(mockPrincipal)
                        .param("appointmentId","1"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("medications"))
                .andExpect(model().attributeExists("appointments"))
                .andExpect(view().name("view-medication"));
    }

    @Test
    void testViewMedications_AsPatient() throws Exception{
        User user=new User(1L,"patient@gmail.com","test","password","Patient");
        Patient patient=new Patient(1L,23,"Female","No history",user,null);
        MedicationDto medicationDto=new MedicationDto(1L,"Paracetamol","500mg","Nothing",1L);

        Principal mockPrincipal=mock(Principal.class);
        when(mockPrincipal.getName()).thenReturn("patient@gmail.com");

        when(userRepository.findByEmail("patient@gmail.com")).thenReturn(Optional.of(user));
        when(medicationService.getMedicationsByAppointment(1L)).thenReturn(medicationDto);
        when(patientRepository.findByUserId(1L)).thenReturn(Optional.of(patient));

        mockMvc.perform(get("/medications/view")
                        .principal(mockPrincipal)
                        .param("appointmentId","1"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("medications"))
                .andExpect(model().attributeExists("appointments"))
                .andExpect(view().name("medications-view"));
    }

    @Test
    void testViewMedications_UserNotFound() throws Exception{
        Principal mockPrincipal=mock(Principal.class);
        when(mockPrincipal.getName()).thenReturn("unknown@gmail.com");

        when(userRepository.findByEmail("unknown@gmail.com")).thenReturn(Optional.empty());

        mockMvc.perform(get("/medications/view")
                        .principal(mockPrincipal)// GET instead of POST
                        .param("appointmentId", "1")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attribute("error","User not found"))
                .andExpect(redirectedUrl("/medications/view"));
    }

    @Test
    void testViewMedications_DoctorNotFound() throws Exception{
        User user=new User(1L,"doctor@gmail.com","test","password","DOCTOR");
        Principal mockPrincipal=mock(Principal.class);
        when(mockPrincipal.getName()).thenReturn("doctor@gmail.com");

        when(userRepository.findByEmail("doctor@gmail.com")).thenReturn(Optional.of(user));
        when(doctorRepository.findByUserId(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/medications/view")
                        .principal(mockPrincipal)// GET instead of POST
                        .param("appointmentId", "1")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attribute("error","Doctor not found"))
                .andExpect(redirectedUrl("/medications/view"));
    }

    @Test
    void testViewMedications_PatientNotFound() throws Exception{
        User user=new User(1L,"patient@gmail.com","test","password","Patient");
        Principal mockPrincipal=mock(Principal.class);
        when(mockPrincipal.getName()).thenReturn("patient@gmail.com");

        when(userRepository.findByEmail("patient@gmail.com")).thenReturn(Optional.of(user));
        when(doctorRepository.findByUserId(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/medications/view")
                        .principal(mockPrincipal)// GET instead of POST
                        .param("appointmentId", "1")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attribute("error","Patient not found"))
                .andExpect(redirectedUrl("/medications/view"));
    }

    @Test
    void testDeleteMedication() throws Exception{
        doNothing().when(medicationService).deleteMedication(1L);

        mockMvc.perform(post("/medications/delete")
                .param("appointmentId","1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/appointments/view"));
    }

}