package com.example.medical.service;

import com.example.medical.dto.UserDto;
import com.example.medical.entity.User;
import com.example.medical.repository.UserRepository;
import com.example.medical.service.impl.user.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private UserServiceImpl userService;

    private UserDto userDto;
    private User user;

    @BeforeEach
    void setUp(){
        userDto=new UserDto(1L,"test@gmail.com","Test","password@123","User");
        user=new User(1L,"test@gmail.com","Test","encodedPassword","User");
    }

    @Test
    void testSaveUser(){
        when(passwordEncoder.encode(userDto.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        User savedUser=userService.save(userDto);

        assertThat(savedUser).isNotNull();
        assertThat(user.getEmail()).isEqualTo(savedUser.getEmail());
        verify(userRepository,times(1)).save(any(User.class));
    }

    @Test
    void testFindByEmail_WhenUserExists(){
        when(userRepository.findByEmail("test@gmail.com")).thenReturn(Optional.of(user));
        when(modelMapper.map(user,UserDto.class)).thenReturn(userDto);

        UserDto foundUser=userService.findByEmail("test@gmail.com");

        assertThat(foundUser).isNotNull();
        assertThat(userDto.getEmail()).isEqualTo(foundUser.getEmail());
        verify(userRepository,times(1)).findByEmail("test@gmail.com");
    }

    @Test
    void testFindByEmail_WhenUserDoesNotExists(){
        when(userRepository.findByEmail("unknown@gmail.com")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, ()-> userService.findByEmail("unknown@gmail.com"));
        verify(userRepository,times(1)).findByEmail("unknown@gmail.com");
    }
}
