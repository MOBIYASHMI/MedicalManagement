package com.example.medical.controller;

import com.example.medical.dto.DoctorDto;
import com.example.medical.dto.UserDto;
import com.example.medical.exceptions.DoctorNotFoundException;
import com.example.medical.service.DoctorService;
import com.example.medical.service.UserService;
import org.hamcrest.Matchers;
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

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(DoctorController.class)
public class DoctorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DoctorService doctorService;

    @MockitoBean
    private UserService userService;

    @InjectMocks
    private DoctorController doctorController;

    @BeforeEach
    void setUp(){
        mockMvc= MockMvcBuilders.standaloneSetup(doctorController)
                .build();
    }

    @Test
    void testSaveDoctorDetails() throws Exception{

        Principal mockPrincipal=mock(Principal.class);
        when(mockPrincipal.getName()).thenReturn("doctor@gmail.com");

        UserDto userDto=new UserDto();
        userDto.setId(1L);

        when(userService.findByEmail("doctor@gmail.com")).thenReturn(userDto);
        when(doctorService.saveDoctorDetails(org.mockito.ArgumentMatchers.any(DoctorDto.class))).thenReturn(new DoctorDto());

        mockMvc.perform(post("/doctors/save-details")
                        .principal(mockPrincipal)
                        .param("userId","1")
                        .param("fullname","Doctor")
                        .param("email","doctor@gmail.com")
                        .param("contactNumber","3032562645")
                        .param("specialization","dermatology"))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("message"))
                .andExpect(redirectedUrl("/doctors/view-details"));

        verify(doctorService,times(1)).saveDoctorDetails(org.mockito.ArgumentMatchers.any(DoctorDto.class));
    }

    @Test
    void testSaveDoctorDetails_UserNotFound() throws Exception{
        Principal mockPrincipal=mock(Principal.class);
        when(mockPrincipal.getName()).thenReturn("unknown@gmail.com");

        when(userService.findByEmail("unknown@gmail.com")).thenReturn(null);

        Exception resolvedException=assertThrows(Exception.class,()->
                mockMvc.perform(post("/doctors/save-details")
                        .principal(mockPrincipal)
                        .param("userId","1")
                        .param("fullname","Doctor")
                        .param("email","unknown@gmail.com")
                        .param("contactNumber","3032562645")
                        .param("specialization","dermatology")));

        assertEquals("User not found", resolvedException.getCause().getMessage());
    }

    @Test
    void testSaveDoctorDetails_ValidationErrors() throws Exception{
        Principal mockPrincipal=mock(Principal.class);
        when(mockPrincipal.getName()).thenReturn("doctor@gmail.com");

        mockMvc.perform(post("/doctors/save-details")
                        .principal(mockPrincipal)
                        .param("fullname","Patient")
                        .param("email","patient@gmail.com")
                        .param("contactNumber","")
                        .param("specialization",""))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasErrors("doctor"))
                .andExpect(view().name("doctor-details"));
    }

    @Test
    void testGetDoctorDetails() throws Exception{
        Principal mockPrincipal=mock(Principal.class);
        when(mockPrincipal.getName()).thenReturn("doctor@gmail.com");

        UserDto userDto=new UserDto(1L,"doctor@gmail.com","Test","password","User");
        DoctorDto doctorDto=new DoctorDto(1L,"dermatology","98265427357",1L,null);

        when(userService.findByEmail("doctor@gmail.com")).thenReturn(userDto);
        when(doctorService.getDoctorDetails(userDto.getId())).thenReturn(doctorDto);

        mockMvc.perform(get("/doctors/view-details")
                        .principal(mockPrincipal))
                .andExpect(status().isOk())
                .andExpect(view().name("doctor-details"))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attributeExists("doctor"))
                .andExpect(model().attribute("user", hasProperty("email",equalTo("doctor@gmail.com"))))
                .andExpect(model().attribute("doctor", Matchers.hasProperty("specialization",Matchers.equalTo("dermatology"))));
    }

    @Test
    void testGetDoctorDetails_UserIsNull() throws Exception {
        Principal mockPrincipal = mock(Principal.class);
        when(mockPrincipal.getName()).thenReturn("unknown@gmail.com");

        when(userService.findByEmail("unknown@gmail.com")).thenReturn(null);

        mockMvc.perform(get("/doctors/view-details")
                        .principal(mockPrincipal))
                .andExpect(status().isOk()) // Page should still load
                .andExpect(view().name("doctor-details"))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attributeExists("doctor"))
                .andExpect(model().attribute("user", Matchers.hasProperty("id", Matchers.nullValue()))); // User should be empty
    }

    @Test
    void testGetDoctorDetails_PatientIsNull() throws Exception {
        Principal mockPrincipal = mock(Principal.class);
        when(mockPrincipal.getName()).thenReturn("doctor@gmail.com");

        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setEmail("doctor@gmail.com");

        when(userService.findByEmail("doctor@gmail.com")).thenReturn(userDto);
        when(doctorService.getDoctorDetails(1L)).thenReturn(null); // No patient details found

        mockMvc.perform(get("/doctors/view-details")
                        .principal(mockPrincipal))
                .andExpect(status().isOk())
                .andExpect(view().name("doctor-details"))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attributeExists("doctor"))
                .andExpect(model().attribute("doctor", Matchers.hasProperty("userId", Matchers.equalTo(1L)))); // Ensures new PatientDto is created
    }

    @Test
    void testGetUpdateAvailability() throws Exception{

        UserDto userDto=new UserDto(1L,"doctor@gmail.com","Test","password","User");
        DoctorDto doctorDto=new DoctorDto(1L,"dermatology","98265427357",1L,null);

        Principal mockPrincipal = mock(Principal.class);
        when(mockPrincipal.getName()).thenReturn("doctor@gmail.com");

        when(doctorService.getDoctorDetails(userDto.getId())).thenReturn(doctorDto);

        mockMvc.perform(get("/doctors/update-availability")
                .principal(mockPrincipal))
                .andExpect(status().isOk())
                .andExpect(view().name("availability-slots"))
                .andExpect(model().attributeExists("doctor"));
    }

    @Test
    void testGetUpdateAvailability_UserIsNull() throws Exception{
        Principal mockPrincipal = mock(Principal.class);
        when(mockPrincipal.getName()).thenReturn("unknown@gmail.com");

        when(userService.findByEmail("unknown@gmail.com")).thenReturn(null);

        mockMvc.perform(get("/doctors/update-availability")
                .principal(mockPrincipal))
                .andExpect(view().name("availability-slots"))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attributeExists("doctor"))
                .andExpect(model().attribute("user",hasProperty("id",Matchers.nullValue())));

    }

    @Test
    void testGetUpdateAvailability_DoctorIsNull() throws Exception{
        UserDto userDto=new UserDto(1L,"doctor@gmail.com","Test","password","User");
        Principal mockPrincipal = mock(Principal.class);
        when(mockPrincipal.getName()).thenReturn("doctor@gmail.com");

        when(userService.findByEmail("doctor@gmail.com")).thenReturn(userDto);
        when(doctorService.getDoctorDetails(1L)).thenReturn(null);

        mockMvc.perform(get("/doctors/update-availability")
                        .principal(mockPrincipal))
                .andExpect(view().name("availability-slots"))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attributeExists("doctor"))
                .andExpect(model().attribute("doctor",hasProperty("userId",Matchers.equalTo(1L))));
    }

    @Test
    void testUpdateAvailability() throws Exception{
         Long doctorId=1L;
         List<String> availableSlots=List.of(String.valueOf(LocalDateTime.now()),String.valueOf(LocalDateTime.now().plusHours(2)));

         mockMvc.perform(post("/doctors/save-availability")
                 .param("doctorId", String.valueOf(doctorId))
                 .param("availableSlots",availableSlots.toArray(new String[0])))
                 .andExpect(status().is3xxRedirection())
                 .andExpect(redirectedUrl("/doctor-page"))
                 .andExpect(flash().attributeExists("message"));
    }

    @Test
    void testUpdateAvailability_DoctorNotFound() throws Exception{
        Long doctorId=99L;
        List<String> availableSlots=List.of(String.valueOf(LocalDateTime.now()),String.valueOf(LocalDateTime.now().plusHours(2)));

        doThrow(new DoctorNotFoundException("Doctor not found")).when(doctorService)
                .updateAvailability(eq(doctorId),anyList());

        mockMvc.perform(post("/doctors/save-availability")
                .param("doctorId",doctorId.toString())
                .param("availableSlots",availableSlots.toArray(new String[0])))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("error"))
                .andExpect(redirectedUrl("/doctor-page"))
                .andExpect(flash().attribute("error","Doctor not found"));
    }

}
