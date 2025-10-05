package com.innowise.internship.authentificationservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
public class RegisterRequestDto {

    @NotBlank(message = "Name cannot be empty")
    private String name;

    @NotBlank(message = "Surname cannot be empty")
    private String surname;

    @NotNull(message = "Birth date cannot be empty")
    @Past(message = "Date must be in past")
    private LocalDate birth_date;

    @NotBlank(message = "Email cannot be empty")
    @Email(message = "Please provide valid email address")
    private String email;

    @NotBlank(message = "Login cannot be empty")
    private String login;

    @NotBlank(message = "Password cannot be empty")
    @Size(min = 8, message = "Password size must be at least 8 characters")
    private String password;
}
