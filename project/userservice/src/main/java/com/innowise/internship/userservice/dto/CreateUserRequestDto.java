package com.innowise.internship.userservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CreateUserRequestDto {

  @NotBlank(message = "Name cannot be empty")
  private String name;

  @NotBlank(message = "Surname cannot be empty")
  private String surname;

  @NotNull(message = "Name cannot be null")
  @Past(message = "Birth date must be in the past")
  private LocalDate birth_date;

  @NotBlank(message = "Name cannot be empty")
  @Email(message = "Please provide a valid email address")
  private String email;
}
