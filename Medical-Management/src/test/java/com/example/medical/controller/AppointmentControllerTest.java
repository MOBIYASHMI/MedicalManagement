package com.example.medical.controller;

import com.example.medical.dto.AppointmentDto;
import com.example.medical.dto.PatientDto;
import com.example.medical.dto.UserDto;
import com.example.medical.entity.Availability;
import com.example.medical.entity.Doctor;
import com.example.medical.entity.Patient;
import com.example.medical.entity.User;
import com.example.medical.exceptions.DoctorNotFoundException;
import com.example.medical.exceptions.PatientNotFoundException;
import com.example.medical.repository.AvailabilityRepository;
import com.example.medical.repository.DoctorRepository;
import com.example.medical.repository.PatientRepository;
import com.example.medical.repository.UserRepository;
import com.example.medical.service.AppointmentService;
import com.example.medical.service.PatientService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.Model;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(AppointmentController.class)
public class AppointmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AppointmentService appointmentService;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private PatientService patientService;

    @MockitoBean
    private PatientRepository patientRepository;

    @MockitoBean
    private DoctorRepository doctorRepository;

    @MockitoBean
    private AvailabilityRepository availabilityRepository;

    @Mock
    private Model model;

    @InjectMocks
    private AppointmentController appointmentController;

    @BeforeEach
    void setUp(){
        mockMvc= MockMvcBuilders.standaloneSetup(appointmentController).build();
    }

    @Test
    void testShowBookingPage() throws Exception{
        UserDto userDto=new UserDto(1L,"patient@gmail.com","Test","password","User");
        User user=new User(1L,"patient@gmail.com","Test","password","User");
        PatientDto patientDto=new PatientDto(1L,23,"female","Mo history",1L);
        Doctor doctor1=new Doctor(1L,"dermatology","8920386577",user,null,null);
        Doctor doctor2=new Doctor(2L,"dermatology","8920386577",user,null,null);

//        AppointmentDto appointmentDto=new AppointmentDto();
//        appointmentDto.setPatientId(patientDto.getUserId());
//        appointmentDto.setStatus("BOOKED");
//        appointmentDto.setMedicationAdded(false);

        Principal mockPrincipal=mock(Principal.class);
        when(mockPrincipal.getName()).thenReturn("patient@gmail.com");

        when(userRepository.findByEmail("patient@gmail.com")).thenReturn(Optional.of(user));
        when(patientService.getPatientDetails(user.getId())).thenReturn(patientDto);
        when(doctorRepository.findAll()).thenReturn(List.of(doctor1,doctor2));

        mockMvc.perform(get("/appointments/book")
                .principal(mockPrincipal))
                .andExpect(status().isOk())
                .andExpect(view().name("appointment-booking"))
                .andExpect(model().attributeExists("patientId"))
                .andExpect(model().attributeExists("doctors"))
                .andExpect(model().attributeExists("appointment"));
    }

    @Test
    void testShowBookingPage_UserNotFound(){
        Principal mockPrincipal = mock(Principal.class);
        when(mockPrincipal.getName()).thenReturn("unknown@gmail.com");

        when(userRepository.findByEmail("unknown@gmail.com")).thenReturn(Optional.empty());

        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () ->
                appointmentController.showBookingPage(model, mockPrincipal)
        );

        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void testBookAppointment_SlotAvailable() throws Exception{
        LocalDateTime fixedTime = LocalDateTime.of(2025, 3, 30, 10, 0);
        AppointmentDto appointmentDto=new AppointmentDto(1L,1L,1L, fixedTime,"BOOKED",null,"DOCTOR","dermatology","Patient",false);

        when(availabilityRepository.existsByDoctorIdAndAvailableSlot(anyLong(),any())).thenReturn(true);
        when(appointmentService.saveAppointment(any(AppointmentDto.class))).thenReturn(new AppointmentDto());
        doNothing().when(appointmentService).deleteAvailability(anyLong(),any());

        mockMvc.perform(post("/appointments/book")
                .param("doctorId","1")
                .param("patientId","1")
                .param("appointmentTime", fixedTime.toString())
                .param("status","BOOKED"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/appointments/view"));

        verify(appointmentService, times(1)).saveAppointment(any(AppointmentDto.class));
        verify(appointmentService, times(1)).deleteAvailability(anyLong(), any());
    }

    @Test
    void testBookAppointment_SlotUnavailable() throws Exception{
        LocalDateTime fixedTime = LocalDateTime.of(2025, 3, 30, 10, 0);
        AppointmentDto appointmentDto=new AppointmentDto(1L,1L,1L, fixedTime,"BOOKED",null,"DOCTOR","dermatology","Patient",false);

        when(availabilityRepository.existsByDoctorIdAndAvailableSlot(anyLong(),any())).thenReturn(false);

        mockMvc.perform(post("/appointments/book")
                        .param("doctorId","1")
                        .param("patientId","1")
                        .param("appointmentTime", fixedTime.toString())
                        .param("status","BOOKED"))
                .andExpect(status().isOk())
                .andExpect(view().name("appointment-booking"))
                .andExpect(model().attribute("error","The selected slot is no longer available. Please choose another."));
    }

    @Test
    void testBookAppointment_ValidationErrors() throws Exception{
        mockMvc.perform(post("/appointments/book")
                .param("doctorId","")
                .param("appointmentTime",""))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasErrors("appointment"))
                .andExpect(view().name("appointment-booking"));
    }

    @Test
    void testViewAppointments_AsDoctor() throws Exception{
        Principal mockPrincipal=mock(Principal.class);
        when(mockPrincipal.getName()).thenReturn("doctor@gmail.com");

        User user=new User(1L,"doctor@gmail.com","DOCTOR","password","DOCTOR");
        Doctor doctor=new Doctor(1L,"Dermatology","9485788954",user,null,null);
        List<AppointmentDto> appointmentDto= List.of(new AppointmentDto(1L, 1L, 2L, LocalDateTime.now(), "BOOKED", null, "DOCTOR", "Dermatology", "Patient", false));

        when(userRepository.findByEmail("doctor@gmail.com")).thenReturn(Optional.of(user));
        when(doctorRepository.findByUserId(1L)).thenReturn(Optional.of(doctor));
        when(appointmentService.getAppointmentsByDoctor(1L)).thenReturn(appointmentDto);

        mockMvc.perform(get("/appointments/view")
                        .principal(mockPrincipal))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("appointments"))
                .andExpect(view().name("doctor-appointments"));
    }

    @Test
    void testViewAppointments_AsPatient() throws Exception{
        Principal mockPrincipal=mock(Principal.class);
        when(mockPrincipal.getName()).thenReturn("patient@gmail.com");

        User user=new User(1L,"patient@gmail.com","PATIENT","password","PATIENT");
        Patient patient=new Patient(1L,23,"Male","No history",user,null);
        List<AppointmentDto> appointmentDto= List.of(new AppointmentDto(1L, 1L, 2L, LocalDateTime.now(), "BOOKED", null, "DOCTOR", "Dermatology", "Patient", false));

        when(userRepository.findByEmail("patient@gmail.com")).thenReturn(Optional.of(user));
        when(patientRepository.findByUserId(1L)).thenReturn(Optional.of(patient));
        when(appointmentService.getAppointmentsByPatient(1L)).thenReturn(appointmentDto);

        mockMvc.perform(get("/appointments/view")
                        .principal(mockPrincipal))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("appointments"))
                .andExpect(view().name("appointments-view"));
    }

    @Test
    void testViewAppointments_UserNotFound() throws Exception{
        Principal mockPrincipal=mock(Principal.class);
        when(mockPrincipal.getName()).thenReturn("unknown@gmail.com");

        when(userRepository.findByEmail("unknown@gmail.com")).thenReturn(Optional.empty());

        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () ->
                appointmentController.showBookingPage(model, mockPrincipal)
        );

        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void testViewAppointments_DoctorNotFound() throws Exception{

        User user=new User(1L,"doctor@gmail.com","DOCTOR","password","DOCTOR");

        when(userRepository.findByEmail("doctor@gmail.com")).thenReturn(Optional.of(user));
        when(doctorRepository.findByUserId(anyLong())).thenThrow(new DoctorNotFoundException("Doctor not found"));

        mockMvc.perform(get("/appointments/view")
                .principal(()-> "doctor@gmail.com"))
                .andExpect(status().isOk())
                .andExpect(view().name("appointments-view"))
                .andExpect(model().attribute("error","Doctor not found"));
    }

    @Test
    void testViewAppointments_PatientNotFound() throws Exception{

        User user=new User(1L,"patient@gmail.com","PATIENT","password","PATIENT");

        when(userRepository.findByEmail("patient@gmail.com")).thenReturn(Optional.of(user));
        when(patientRepository.findByUserId(anyLong())).thenThrow(new PatientNotFoundException("Patient not found"));

        mockMvc.perform(get("/appointments/view")
                        .principal(()-> "patient@gmail.com"))
                .andExpect(status().isOk())
                .andExpect(view().name("appointments-view"))
                .andExpect(model().attribute("error","Patient not found"));
    }

    @Test
    void testCancelAppointment() throws Exception{
        User user=new User(1L,"doctor@gmail.com","DOCTOR","password","DOCTOR");
        AppointmentDto appointmentDto=new AppointmentDto(1L,1L,1L,LocalDateTime.now().plusDays(4),"BOOKED",null,"Doctor","Dermatology","Patient",false);
        Doctor doctor=new Doctor(1L,"Dermatology","9485788954",user,null,null);
        Availability mockAvailability = new Availability(appointmentDto.getAppointmentTime(), doctor);

        when(appointmentService.getAppointmentDetails(appointmentDto.getId())).thenReturn(appointmentDto);
        doNothing().when(appointmentService).cancelAppointment(appointmentDto.getId());
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));
        when(availabilityRepository.save(any())).thenReturn(mockAvailability);

        mockMvc.perform(post("/appointments/cancel")
                .param("appointmentId","1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/appointments/view"));

    }

    @Test
    void testCancelAppointment_AppointmentNotFound() throws Exception{

        when(appointmentService.getAppointmentDetails(anyLong())).thenReturn(null);

        mockMvc.perform(post("/appointments/cancel")
                        .param("appointmentId","1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCancelAppointment_DoctorNotFound() throws Exception{

        AppointmentDto appointmentDto=new AppointmentDto(1L,1L,1L,LocalDateTime.now().plusDays(4),"BOOKED",null,"Doctor","Dermatology","Patient",false);

        when(appointmentService.getAppointmentDetails(anyLong())).thenReturn(appointmentDto);
        when(doctorRepository.findById(anyLong())).thenReturn(Optional.empty());

        mockMvc.perform(post("/appointments/cancel")
                        .param("appointmentId", "1")
                        .with(csrf()))
                .andExpect(status().isOk())  // Now it directly returns a view, so expect 200 OK
                .andExpect(view().name("doctor-appointments"))  // Expect "doctor-appointments" view
                .andExpect(model().attributeExists("error"))  // Expect error message in model
                .andExpect(model().attribute("error", "Doctor not found")); // Check that it returns the correct view
    }
}