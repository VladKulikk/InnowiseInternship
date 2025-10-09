package com.innowise.internship.orderservice.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class UserResponseDto {
    private Long id;
    private String name;
    private String surname;
    private LocalDate birth_date;
    private String email;
}
