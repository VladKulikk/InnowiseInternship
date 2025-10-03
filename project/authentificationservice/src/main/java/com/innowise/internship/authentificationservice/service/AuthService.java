package com.innowise.internship.authentificationservice.service;

import com.innowise.internship.authentificationservice.dto.AuthRequestDto;
import com.innowise.internship.authentificationservice.dto.AuthResponseDto;
import com.innowise.internship.authentificationservice.dto.RegisterRequestDto;
import jakarta.security.auth.message.AuthException;

public interface AuthService {
    void register(RegisterRequestDto registerRequestDto);
    AuthResponseDto login(AuthRequestDto authRequestDto) throws AuthException;
    AuthResponseDto refresh(String refreshToken) throws AuthException;
    boolean validate(String accessToken);
}
