package com.innowise.internship.userservice.service;

import com.innowise.internship.userservice.dto.CreateUserRequestDto;
import com.innowise.internship.userservice.dto.UserResponseDto;
import com.innowise.internship.userservice.mapper.UserMapper;
import com.innowise.internship.userservice.model.User;
import com.innowise.internship.userservice.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Transactional
    @Override
    public UserResponseDto createUser(CreateUserRequestDto requestDto) {

        if (userRepository.findByEmail(requestDto.getEmail()).isPresent()) {
            throw new IllegalStateException("User with email" + requestDto.getEmail() + " already exists");
        }

        User user = userMapper.toEntity(requestDto);
        User savedUser = userRepository.save(user);

        return userMapper.toDto(savedUser);
    }

    @Transactional(readOnly = true)
    @Override
    public UserResponseDto getUserById(Long id) {
        return userMapper.toDto(findUserOrThrow(id));
    }

    @Transactional(readOnly = true)
    @Override
    public List<UserResponseDto> getAllUsers() {
        return userRepository.findAll().stream().map(userMapper::toDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public List<UserResponseDto> getUsersByIds(List<Long> ids) {
        List<User> users = userRepository.findAllByIdIn(ids);

        return users.stream().map(userMapper::toDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public UserResponseDto getUserByEmail(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new IllegalStateException("User with email" + email + " not found"));

        return userMapper.toDto(user);
    }

    @Transactional
    @Override
    public UserResponseDto updateUser(Long id, CreateUserRequestDto requestDto) {
        User existingUser = findUserOrThrow(id);

        existingUser.setName(requestDto.getName());
        existingUser.setSurname(requestDto.getSurname());
        existingUser.setEmail(requestDto.getEmail());
        existingUser.setBirth_date(requestDto.getBirth_date());

        User updatedUser = userRepository.save(existingUser);
        return userMapper.toDto(updatedUser);
    }

    @Transactional
    @Override
    public void deleteUser(Long id) {
        userRepository.delete(findUserOrThrow(id));
    }

    private User findUserOrThrow(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new IllegalStateException("User with id " + id + " not found"));
    }
}
