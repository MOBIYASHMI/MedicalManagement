package com.example.medical.service.impl.user;

import com.example.medical.dto.UserDto;
import com.example.medical.entity.User;
import com.example.medical.repository.UserRepository;
import com.example.medical.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public User save(UserDto userDto) {
        User user=new User(userDto.getId(),userDto.getEmail(),userDto.getFullname(),passwordEncoder.encode(userDto.getPassword()),userDto.getRole());
        return userRepository.save(user);
    }

    @Override
    public UserDto findByEmail(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        return modelMapper.map(user, UserDto.class);
    }


}
