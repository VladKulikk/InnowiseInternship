package com.innowise.internship.userservice;

import com.innowise.internship.userservice.dto.CreateUserRequestDto;
import com.innowise.internship.userservice.dto.UserResponseDto;
import com.innowise.internship.userservice.exception.DuplicateResourceException;
import com.innowise.internship.userservice.exception.ResourceNotFoundException;
import com.innowise.internship.userservice.mapper.UserMapper;
import com.innowise.internship.userservice.model.User;
import com.innowise.internship.userservice.repository.UserRepository;
import com.innowise.internship.userservice.service.UserServiceImpl;
import liquibase.exception.DuplicateChangeSetException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

  @Mock private UserRepository userRepository;

  @Mock private UserMapper userMapper;

  @InjectMocks private UserServiceImpl userService;

  @Test
  public void createUser_whenEmailIsUnique_shouldCreateAndReturnUser() {
    CreateUserRequestDto requestDto = new CreateUserRequestDto();
    requestDto.setEmail("email@email.com");

    User userToSave = new User();
    User savedUser = new User();
    UserResponseDto expectedDto = new UserResponseDto();

    when(userRepository.findByEmail(requestDto.getEmail())).thenReturn(Optional.empty());
    when(userMapper.toEntity(requestDto)).thenReturn(userToSave);
    when(userRepository.save(userToSave)).thenReturn(savedUser);
    when(userMapper.toDto(savedUser)).thenReturn(expectedDto);

    UserResponseDto actualDto = userService.createUser(requestDto);

    assertThat(actualDto).isEqualTo(expectedDto);
    verify(userRepository).save(userToSave);
  }

  @Test
  public void createUser_whenEmailExists_shouldThrowDuplicateResourceException() {
    CreateUserRequestDto requestDto = new CreateUserRequestDto();
    requestDto.setEmail("exists@email.com");

    when(userRepository.findByEmail(requestDto.getEmail())).thenReturn(Optional.of(new User()));

    assertThrows(DuplicateResourceException.class, () -> userService.createUser(requestDto));
    verify(userRepository, never()).save(any());
  }

  @Test
  public void getUserById_whenUserExists_shouldReturnUserDto() {
    long userId = 1L;
    User user = new User();
    UserResponseDto expectedDto = new UserResponseDto();

    when(userRepository.findById(userId)).thenReturn(Optional.of(user));
    when(userMapper.toDto(user)).thenReturn(expectedDto);

    UserResponseDto actualDto = userService.getUserById(userId);

    assertThat(actualDto).isEqualTo(expectedDto);
  }

  @Test
  public void getUserById_whenUserDoesNotFound_shouldThrowResourceNotFoundException() {
    long userId = 99L;
    when(userRepository.findById(userId)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> userService.getUserById(userId));
  }

  @Test
  public void getUsersByIds_whenUsersExists_shouldReturnDtoList() {
    List<Long> ids = List.of(1L, 2L);
    User user1 = new User();
    User user2 = new User();
    List<User> users = List.of(user1, user2);

    when(userRepository.findAllByIdIn(ids)).thenReturn(users);

    List<UserResponseDto> actualDtos = userService.getUsersByIds(ids);

    assertThat(actualDtos).isNotNull();
    assertThat(actualDtos.size()).isEqualTo(2);
    verify(userRepository).findAllByIdIn(ids);
  }

  @Test
  public void getUserByEmail_whenUserExists_shouldReturnUserDto() {
    String email = "test@email.com";
    User user = new User();
    UserResponseDto expectedDto = new UserResponseDto();

    when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
    when(userMapper.toDto(user)).thenReturn(expectedDto);

    UserResponseDto actualDto = userService.getUserByEmail(email);

    assertThat(actualDto).isEqualTo(expectedDto);
  }

  @Test
  public void getUserByEmail_whenUserDoesNotFound_shouldThrowResourceNotFoundException() {
    String email = "test@email.com";
    when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> userService.getUserByEmail(email));
  }

  @Test
  public void updateUser_whenDataIsValid_shouldUpdateAndReturnUserDto() {
    long userId = 1L;
    CreateUserRequestDto requestDto = new CreateUserRequestDto();
    requestDto.setName("Jane");

    User existingUser = new User();
    existingUser.setId(userId);
    existingUser.setName("John");

    UserResponseDto expectedDto = new UserResponseDto();

    when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
    when(userRepository.save(existingUser)).thenReturn(existingUser);
    when(userMapper.toDto(existingUser)).thenReturn(expectedDto);

    userService.updateUser(userId, requestDto);

    ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
    verify(userRepository).save(captor.capture());

    assertThat(captor.getValue().getName()).isEqualTo("Jane");
  }

  @Test
  public void deleteUser_whenUserExists_shouldDeleteUser() {
    long userId = 1L;
    User existingUser = new User();

    when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));

    userService.deleteUser(userId);

    verify(userRepository, times(1)).delete(existingUser);
  }
}
