package com.innowise.internship.userservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.innowise.internship.userservice.dto.CreateUserRequestDto;
import com.innowise.internship.userservice.model.User;
import com.innowise.internship.userservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class UserControllerIntegrationTest extends AbstractIntegrationTest{

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @Autowired private UserRepository userRepository;

  // cleaning db before each test
  @BeforeEach
  public void setup() {
    userRepository.deleteAll();
  }

  @Test
  public void createUser_whenDataIsValid_shouldSaveUserAndReturn201() throws Exception {
    CreateUserRequestDto requestDto = initUserRequestDto("TestName","TestEmail@email.com");

    mockMvc
        .perform(
            post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").exists())
        .andExpect(jsonPath("$.name").value("TestName"))
        .andExpect(jsonPath("$.email").value("TestEmail@email.com"));
  }

  @Test
  public void createUser_whenEmailIsInvalid_shouldReturn400() throws Exception {
      CreateUserRequestDto requestDto = initUserRequestDto("TestName","notValidEmail");

      mockMvc.perform(
          post("/api/v1/users").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(requestDto)))
              .andExpect(status().isBadRequest());
  }

  @Test
  public void getUserById_whenDataIsValid_shouldReturnUser() throws Exception {
    User savedUser = userRepository.save(initUser("TestEmail@email.com"));

    mockMvc
        .perform(get("/api/v1/users/{id}", savedUser.getId()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(savedUser.getId()))
        .andExpect(jsonPath("$.email").value("TestEmail@email.com"));
  }

  @Test
  public void getUserById_whenDoesNotExist_shouldReturn404() throws Exception {
    Long nonExistentUserId = 99L;

    mockMvc
        .perform(get("/api/v1/users/{id}", nonExistentUserId))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").value("User with id " + nonExistentUserId + " not found"));
  }

  @Test
  public void getUserByEmail_whenUserExists_shouldReturnUser() throws Exception {
    User savedUser = userRepository.save(initUser("TestEmail@email.com"));

    mockMvc
        .perform(get("/api/v1/users/by-email").param("email", "TestEmail@email.com"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.email").value(savedUser.getEmail()));
  }

  @Test
  public void getUserByEmail_whenUserDoesNotExist_shouldReturn404() throws Exception {
    String nonExistentUserEmail = "nonExistentEmail@email.com";

    mockMvc
        .perform(get("/api/v1/users/by-email").param("email", nonExistentUserEmail))
        .andExpect(status().isNotFound())
        .andExpect(
            jsonPath("$.message").value("User with email " + nonExistentUserEmail + " not found"));
  }

  @Test
  public void getUsersByIds_whenUsersExists_shouldReturnUsersList() throws Exception {
    User user1 = initUser("TestEmail1@email.com");
    User user2 = initUser("TestEmail2@email.com");
    userRepository.saveAll(List.of(user1, user2));

    mockMvc
        .perform(
            get("/api/v1/users")
                .param("ids", user1.getId().toString() + "," + user2.getId().toString()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(2));
  }

  @Test
  public void updateUser_whenUserExists_shouldUpdateUser() throws Exception {
    User savedUser = userRepository.save(initUser("OldEmail@email.com"));

      CreateUserRequestDto requestDto = initUserRequestDto("NewName","NewEmail@email.com");

    mockMvc
        .perform(
            put("/api/v1/users/{id}", savedUser.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("NewName"))
        .andExpect(jsonPath("$.email").value("NewEmail@email.com"));
  }

  @Test
  public void updateUser_whenEmailIsTakenByAnotherUser_shouldReturnError() throws Exception {
    User user1 = userRepository.save(initUser("email@email.com"));
    userRepository.save(initUser("user@email.com"));

    CreateUserRequestDto requestDto = initUserRequestDto("NewName","user@email.com");

      mockMvc.perform(
            put("/api/v1/users/{id}", user1.getId()).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(requestDto)))
              .andExpect(status().isConflict());
  }

  @Test
  public void deleteUser_whenUserExists_shouldReturn204() throws Exception {
    User savedUser = userRepository.save(initUser("TestEmail@email.com"));

    mockMvc
        .perform(delete("/api/v1/users/{id}", savedUser.getId()))
        .andExpect(status().isNoContent());

    assertThat(userRepository.findById(savedUser.getId())).isEmpty();
  }

  public User initUser(String email){
      User user = new User();
      user.setName("TestName");
      user.setSurname("TestSurname");
      user.setEmail(email);
      user.setBirth_date(LocalDate.of(1990, 1, 1));

      return user;
  }

  public CreateUserRequestDto initUserRequestDto(String name, String email){
      CreateUserRequestDto requestDto = new CreateUserRequestDto();
      requestDto.setName(name);
      requestDto.setSurname("TestSurname");
      requestDto.setEmail(email);
      requestDto.setBirth_date(LocalDate.of(1990, 1, 1));

      return requestDto;
  }
}
