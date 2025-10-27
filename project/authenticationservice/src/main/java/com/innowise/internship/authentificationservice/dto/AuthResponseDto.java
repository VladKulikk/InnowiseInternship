package com.innowise.internship.authentificationservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponseDto {

    private Long userId;
    private String accessToken;
    private String refreshToken;

}
