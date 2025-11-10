package com.innowise.internship.authentificationservice.service;

import com.innowise.internship.authentificationservice.client.UserServiceClient;
import com.innowise.internship.authentificationservice.dto.AuthRequestDto;
import com.innowise.internship.authentificationservice.dto.AuthResponseDto;
import com.innowise.internship.authentificationservice.dto.RegisterRequestDto;
import com.innowise.internship.authentificationservice.dto.UserResponseDto;
import com.innowise.internship.authentificationservice.exception.DuplicateResourceException;
import com.innowise.internship.authentificationservice.exception.RegistrationFailedException;
import com.innowise.internship.authentificationservice.model.UserCredentials;
import com.innowise.internship.authentificationservice.repository.UserCredentialsRepository;
import com.innowise.internship.authentificationservice.security.JwtProvider;
import jakarta.security.auth.message.AuthException;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

  private final UserCredentialsRepository userCredentialsRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtProvider jwtProvider;
  private final UserServiceClient userServiceClient;



  @Transactional
  @Override
  public void register(RegisterRequestDto requestDto) {
    userCredentialsRepository
        .findByLogin(requestDto.getLogin())
        .ifPresent(
            user -> {
              throw new DuplicateResourceException(
                  "User with login " + requestDto.getLogin() + " already exists");
            });

    Long createdUserId = null;

    try {
      createdUserId = createUserInUserService(requestDto);
      saveUserCredentials(requestDto, createdUserId);
    } catch (Exception e) {
      if (createdUserId != null) {
        try {
          userServiceClient.rollbackUserCreation(createdUserId);
        } catch (Exception ex) {
          throw new RuntimeException(
              "Failed to rollback user creation for user with id " + createdUserId, ex);
        }
      }
      throw new RegistrationFailedException("Registration failed");
    }
  }

  @Override
  public AuthResponseDto login(AuthRequestDto requestDto) throws AuthException {
    UserCredentials userCredentials =
        userCredentialsRepository
            .findByLogin(requestDto.getLogin())
            .orElseThrow(() -> new AuthException("Invalid login or password"));

    if (!passwordEncoder.matches(requestDto.getPassword(), userCredentials.getPasswordHash())) {
      throw new AuthException("Invalid login or password");
    }

    String accessToken = jwtProvider.generateAccessToken(userCredentials.getLogin());
    String refreshToken = jwtProvider.generateRefreshToken(userCredentials.getLogin());

    return new AuthResponseDto(userCredentials.getUserId(), accessToken, refreshToken);
  }

  @Override
  public AuthResponseDto refresh(String refreshToken) throws AuthException {
    if (!jwtProvider.validateRefreshToken(refreshToken)) {
      throw new AuthException("Invalid refresh token");
    }

    final String login = jwtProvider.getLoginFromRefreshToken(refreshToken);
    final UserCredentials credentials =
        userCredentialsRepository
            .findByLogin(login)
            .orElseThrow(() -> new AuthException("User not found"));
    final String newAccessToken = jwtProvider.generateAccessToken(login);
    final String newRefreshToken = jwtProvider.generateRefreshToken(login);

    return new AuthResponseDto(credentials.getUserId(), newAccessToken, newRefreshToken);
  }

  @Override
  public boolean validate(String accessToken) {
    return jwtProvider.validateAccessToken(accessToken);
  }

  private Long createUserInUserService(RegisterRequestDto requestDto) {
    // this map represented JSON to userservice
    Map<String, Object> userCreationPayload = new HashMap<>();
    userCreationPayload.put("name", requestDto.getName());
    userCreationPayload.put("surname", requestDto.getSurname());
    userCreationPayload.put("email", requestDto.getEmail());
    userCreationPayload.put("birth_date", requestDto.getBirth_date());

    UserResponseDto createdUser = userServiceClient.createUser(userCreationPayload);

    if (createdUser == null) {
      throw new RuntimeException("Failed to create user profile");
    }
    if (createdUser.getId() == null) {
      throw new RuntimeException("Failed to get id of user");
    }

    return createdUser.getId();
  }

  private void saveUserCredentials(RegisterRequestDto requestDto, Long userId) {
    UserCredentials userCredentials = new UserCredentials();
    userCredentials.setUserId(userId);
    userCredentials.setLogin(requestDto.getLogin());
    userCredentials.setPasswordHash(passwordEncoder.encode(requestDto.getPassword()));

    userCredentialsRepository.save(userCredentials);
  }
}
