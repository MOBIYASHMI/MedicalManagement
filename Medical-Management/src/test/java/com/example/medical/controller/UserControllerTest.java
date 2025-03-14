package com.example.medical.controller;

import com.example.medical.dto.UserDto;
import com.example.medical.entity.User;
import com.example.medical.repository.UserRepository;
import com.example.medical.service.PatientService;
import com.example.medical.service.UserService;
import com.example.medical.service.impl.user.CustomUserDetail;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.security.Principal;
import java.util.Optional;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private PatientService patientService;

    @MockitoBean
    private UserService userService;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @BeforeEach
    void setUp(){
        mockMvc= MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    void testSignUpPage() throws Exception{
        mockMvc.perform(get("/signup"))
                .andExpect(status().isOk())
                .andExpect(view().name("signup"))
                .andExpect(model().attributeExists("user"));
    }

    @Test
    void testSaveUser_SuccessfulRegistration() throws Exception{
        UserDto userDto=new UserDto(1L,"test@gmail.com","Test","password","User");

        User user=new User(1l,"test@gmail.com","Test","password","User");

        when(userRepository.findByEmail(userDto.getEmail())).thenReturn(Optional.empty());
        when(userService.save(userDto)).thenReturn(user);

        mockMvc.perform(post("/signup")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("email","test@gmail.com")
                .param("fullname","Test")
                .param("role","User")
                .param("password","password"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    void testSaveUser_EmailAlreadyExists()throws Exception{
        UserDto userDto=new UserDto();
        userDto.setEmail("existing@example.com");

        when(userRepository.findByEmail(userDto.getEmail())).thenReturn(Optional.of(new User()));

        mockMvc.perform(post("/signup")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("email","existing@example.com"))
                .andExpect(status().isOk())
                .andExpect(view().name("signup"))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attributeHasFieldErrors("user","email"));

    }

    @Test
    void testSaveUser_ValidationErrors() throws Exception{
        mockMvc.perform(post("/signup")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("email","")
                .param("fullname","")
                .param("role","")
                .param("password",""))
                .andExpect(status().isOk())
                .andExpect(view().name("signup"))
                .andExpect(model().attributeHasFieldErrors("user","email","fullname","role","password"));
    }

    @Test
    void testSaveUser_InvalidEmailFormat() throws Exception{
        mockMvc.perform(post("/signup")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("email","invalid-email"))
                .andExpect(status().isOk())
                .andExpect(view().name("signup"))
                .andExpect(model().attributeHasFieldErrors("user","email"));
    }

    @Test
    void testSaveUser_ShortPassword() throws Exception{
        mockMvc.perform(post("/signup")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("password","123"))
                .andExpect(status().isOk())
                .andExpect(view().name("signup"))
                .andExpect(model().attributeHasFieldErrors("user","password"));
    }

    @Test
    void testLogin() throws Exception{
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"));
    }

//    @Test
//    void testLogin_AuthenticationError() throws Exception{
//        mockMvc.perform(post("/login")
//                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)// 🔹 Simulate form submission
//                        .param("username", "wrong@example.com")
//                        .param("password", "wrongpassword"))
//                .andExpect(status().is3xxRedirection())  // 🔹 Expect redirection to login page
//                .andExpect(redirectedUrl("/error"));
//    }

    @Test
    void testDoctorPage() throws Exception{
        Principal mockPrincipal=mock(Principal.class);
        when(mockPrincipal.getName()).thenReturn("doctor@example.com");

        User mockUser=new User();
        mockUser.setFullname("doctor");
        mockUser.setEmail("doctor@gmail.com");
        CustomUserDetail mockUserDetails=new CustomUserDetail(mockUser);

        when(userDetailsService.loadUserByUsername("doctor@example.com")).thenReturn(mockUserDetails);

        mockMvc.perform(get("/doctor-page")
                .principal(mockPrincipal))
                .andExpect(status().isOk())
                .andExpect(view().name("doctor-page"))
                .andExpect(model().attributeExists("user"));
    }

    @Test
    void testPatientPage() throws Exception{
        Principal mockPrincipal=mock(Principal.class);
        when(mockPrincipal.getName()).thenReturn("patient@example.com");

        User mockUser=new User();
        mockUser.setFullname("patient");
        mockUser.setEmail("patient@gmail.com");
        CustomUserDetail mockUserDetails=new CustomUserDetail(mockUser);

        when(userDetailsService.loadUserByUsername("patient@example.com")).thenReturn(mockUserDetails);

        mockMvc.perform(get("/patient-page")
                        .principal(mockPrincipal))
                .andExpect(status().isOk())
                .andExpect(view().name("patient-page"))
                .andExpect(model().attributeExists("user"));
    }

}
