package com.innowise.internship.authentificationservice.controller;

import com.innowise.internship.authentificationservice.dto.AuthRequestDto;
import com.innowise.internship.authentificationservice.dto.AuthResponseDto;
import com.innowise.internship.authentificationservice.dto.RefreshTokenRequestDto;
import com.innowise.internship.authentificationservice.dto.RegisterRequestDto;
import com.innowise.internship.authentificationservice.service.AuthService;
import jakarta.security.auth.message.AuthException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

  private final AuthService authService;

  @PostMapping("/register")
  public ResponseEntity<Void> register(@Valid @RequestBody RegisterRequestDto requestDto) {
    authService.register(requestDto);
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  @PostMapping("/login")
  public ResponseEntity<AuthResponseDto> login(@Valid @RequestBody AuthRequestDto requestDto)
      throws AuthException {
    AuthResponseDto authResponse = authService.login(requestDto);
    return ResponseEntity.ok(authResponse);
  }

  @PostMapping("/refresh")
  public ResponseEntity<AuthResponseDto> refresh(@RequestBody RefreshTokenRequestDto requestDto)
      throws AuthException {
    AuthResponseDto response = authService.refresh(requestDto.getRefreshToken());
    return ResponseEntity.ok(response);
  }

  @GetMapping("/validate")
  public ResponseEntity<Boolean> validate(@RequestParam String accessToken) {
    boolean isValid = authService.validate(accessToken);
    return ResponseEntity.ok(isValid);
  }
}
