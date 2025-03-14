package com.example.medical.service;

import com.example.medical.dto.UserDto;
import com.example.medical.entity.User;

public interface UserService {

    User save(UserDto userDto);
    UserDto findByEmail(String email);
}
