package com.example.medical.controller;

import com.example.medical.dto.PatientDto;
import com.example.medical.dto.UserDto;
import com.example.medical.service.PatientService;
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

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(PatientController.class)
public class PatientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PatientService patientService;

    @MockitoBean
    private UserService userService;

    @InjectMocks
    private PatientController patientController;

    @BeforeEach
    void setUp(){
        mockMvc= MockMvcBuilders.standaloneSetup(patientController)
                .build();
    }

    @Test
    void testSavePatientDetails() throws Exception{

        Principal mockPrincipal=mock(Principal.class);
        when(mockPrincipal.getName()).thenReturn("patient@gmail.com");

        UserDto userDto=new UserDto();
        userDto.setId(1L);

        when(userService.findByEmail("patient@gmail.com")).thenReturn(userDto);
        when(patientService.savePatientDetails(org.mockito.ArgumentMatchers.any(PatientDto.class))).thenReturn(new PatientDto());

        mockMvc.perform(post("/patients/save-details")
                .principal(mockPrincipal)
                        .param("userId","1")
                        .param("fullname","Patient")
                        .param("email","patient@gmail.com")
                        .param("age","30")
                        .param("gender","female")
                        .param("medicalHistory","No history"))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("message"))
                .andExpect(redirectedUrl("/patients/view-details"));

        verify(patientService,times(1)).savePatientDetails(org.mockito.ArgumentMatchers.any(PatientDto.class));
    }

    @Test
    void testSavePatientDetails_UserNotFound() throws Exception{
        Principal mockPrincipal=mock(Principal.class);
        when(mockPrincipal.getName()).thenReturn("unknown@gmail.com");

        when(userService.findByEmail("unknown@gmail.com")).thenReturn(null);

        Exception resolvedException=assertThrows(Exception.class,()->
                mockMvc.perform(post("/patients/save-details")
                        .principal(mockPrincipal)
                        .param("userId","1")
                        .param("fullname","Patient")
                        .param("email","unknown@gmail.com")
                        .param("age","30")
                        .param("gender","female")
                        .param("medicalHistory","No history")));

        assertEquals("User not found", resolvedException.getCause().getMessage());
    }

    @Test
    void testSavePatientDetails_ValidationErrors() throws Exception{
        Principal mockPrincipal=mock(Principal.class);
        when(mockPrincipal.getName()).thenReturn("patient@gmail.com");

        mockMvc.perform(post("/patients/save-details")
                .principal(mockPrincipal)
                .param("fullname","Patient")
                .param("email","patient@gmail.com")
                .param("age","")
                .param("gender","")
                .param("medicalHistory","No history"))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasErrors("patient"))
                .andExpect(view().name("patient-details"));
    }

    @Test
    void testGetPatientDetails() throws Exception{
        Principal mockPrincipal=mock(Principal.class);
        when(mockPrincipal.getName()).thenReturn("patient@gmail.com");

        UserDto userDto=new UserDto(1L,"test@gmail.com","Test","password","User");
        PatientDto patientDto=new PatientDto(1L,23,"female","No history",1L);

        when(userService.findByEmail("patient@gmail.com")).thenReturn(userDto);
        when(patientService.getPatientDetails(userDto.getId())).thenReturn(patientDto);

        mockMvc.perform(get("/patients/view-details")
                .principal(mockPrincipal))
                .andExpect(status().isOk())
                .andExpect(view().name("patient-details"))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attributeExists("patient"))
                .andExpect(model().attribute("user", hasProperty("email",equalTo("test@gmail.com"))))
                .andExpect(model().attribute("patient",Matchers.hasProperty("age",Matchers.equalTo(23))));
    }

    @Test
    void testGetPatientDetails_UserNotFound() throws Exception {
        Principal mockPrincipal = mock(Principal.class);
        when(mockPrincipal.getName()).thenReturn("unknown@gmail.com");

        when(userService.findByEmail("unknown@gmail.com")).thenReturn(null);

        mockMvc.perform(get("/patients/view-details")
                        .principal(mockPrincipal))
                .andExpect(status().isOk()) // Page should still load
                .andExpect(view().name("patient-details"))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attributeExists("patient"))
                .andExpect(model().attribute("user", Matchers.hasProperty("id", Matchers.nullValue()))); // User should be empty
    }

    @Test
    void testGetPatientDetails_PatientNotFound() throws Exception {
        Principal mockPrincipal = mock(Principal.class);
        when(mockPrincipal.getName()).thenReturn("patient@gmail.com");

        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setEmail("patient@gmail.com");

        when(userService.findByEmail("patient@gmail.com")).thenReturn(userDto);
        when(patientService.getPatientDetails(1L)).thenReturn(null); // No patient details found

        mockMvc.perform(get("/patients/view-details")
                        .principal(mockPrincipal))
                .andExpect(status().isOk())
                .andExpect(view().name("patient-details"))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attributeExists("patient"))
                .andExpect(model().attribute("patient", Matchers.hasProperty("userId", Matchers.equalTo(1L)))); // Ensures new PatientDto is created
    }


}
