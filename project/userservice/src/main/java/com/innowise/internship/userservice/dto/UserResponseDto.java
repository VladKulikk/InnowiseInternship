package com.innowise.internship.userservice.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

@Data
public class UserResponseDto implements Serializable {
  private Long id;
  private String name;
  private String surname;
  private LocalDate birth_date;
  private String email;
}
