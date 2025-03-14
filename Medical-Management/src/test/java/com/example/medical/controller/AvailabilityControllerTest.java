package com.example.medical.controller;

import com.example.medical.dto.AvailabilityDto;
import com.example.medical.entity.Doctor;
import com.example.medical.repository.DoctorRepository;
import com.example.medical.service.AvailabilityService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.ui.Model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(AvailabilityController.class)
public class AvailabilityControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AvailabilityService availabilityService;

    @MockitoBean
    private Model model;

    @MockitoBean
    private DoctorRepository doctorRepository;

    @InjectMocks
    private AvailabilityController availabilityController;

    @Test
    @WithMockUser(username = "doctor@example.com", roles = {"DOCTOR"})
    void testAddAvailability() throws Exception{
        AvailabilityDto availabilityDto=new AvailabilityDto(1L, LocalDateTime.now().plusDays(2),1L);

        doNothing().when(availabilityService).addAvailability(anyLong(),anyList());

        mockMvc.perform(post("/availability/add")
                .param("doctorId","1")
                .flashAttr("availability",availabilityDto)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attribute("message", "Availability added successfully."))
                .andExpect(redirectedUrl("/doctors/update-availability"));
    }

    @Test
    @WithMockUser(username = "doctor@example.com", roles = {"DOCTOR"})
    void testAddAvailability_ValidationErrors() throws Exception{
        mockMvc.perform(post("/availability/add")
                        .param("doctorId", "1") // Missing availabilityDto fields
                        .flashAttr("availability", new AvailabilityDto()) // Invalid DTO to trigger validation error
                        .with(csrf()))
                .andExpect(status().isOk())  // Form should reload with errors
                .andExpect(view().name("availability-slots"))
                .andExpect(model().attributeExists("error")); // Error flag should be present
    }

    @Test
    @WithMockUser(username = "doctor@example.com", roles = {"DOCTOR"})
    void testGetAvailableSlots() throws Exception {
        List<AvailabilityDto> mockAvailability = List.of(
                new AvailabilityDto(1L, LocalDateTime.of(2025, 3, 20, 10, 0), 1L),
                new AvailabilityDto(2L, LocalDateTime.of(2025, 3, 21, 15, 30), 1L)
        );

        when(availabilityService.getAvailabilityByDoctor(1L)).thenReturn(mockAvailability);

        mockMvc.perform(get("/availability/available-slots")
                .param("doctorId","1"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1));
    }
}
