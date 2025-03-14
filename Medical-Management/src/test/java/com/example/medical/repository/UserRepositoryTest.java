package com.example.medical.repository;

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
class UserRepositoryTest {

    @Mock
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    public void setUp(){
        user = new User();
        user.setEmail("test@example.com");
        user.setFullname("Test User");
        user.setPassword("password@123");
        user.setRole("USER");

    }

    @Test
    void testFindByEmail_WhenUserExists() {
        // Arrange
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        Optional<User> foundUser=userRepository.findByEmail("test@example.com");

        assertThat(foundUser).isNotNull();
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getEmail()).isEqualTo("test@example.com");

        verify(userRepository,times(1)).findByEmail("test@example.com");

    }

    @Test
    void testFindByEmail_WhenUserDoesNotExist() {
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());
        // Act
        Optional<User> foundUser = userRepository.findByEmail("nonexistent@example.com");

        // Assert
        assertThat(foundUser).isEmpty();

        verify(userRepository,times(1)).findByEmail("nonexistent@example.com");

    }

    @Test
    void testSaveUser(){
        when(userRepository.save(any(User.class))).thenReturn(user);

        User savedUser=userRepository.save(user);

        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getEmail()).isEqualTo("test@example.com");

        verify(userRepository,times(1)).save(user);
    }
}
