package com.innowise.internship.userservice.service;

import com.innowise.internship.userservice.dto.CreateUserRequestDto;
import com.innowise.internship.userservice.dto.UserResponseDto;
import com.innowise.internship.userservice.model.User;

import java.util.List;

public interface UserService {
  UserResponseDto createUser(CreateUserRequestDto requestDto);

  UserResponseDto getUserById(Long id);

  List<UserResponseDto> getUsersByIds(List<Long> ids);

  UserResponseDto getUserByEmail(String email);

  UserResponseDto updateUser(Long id, CreateUserRequestDto requestDto);

  void deleteUser(Long id);

  User findUserEntityById(Long id);
}
