package com.innowise.internship.authentificationservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RefreshTokenRequestDto {

    @NotBlank
    private String refreshToken;
}
