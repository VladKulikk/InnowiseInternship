package com.innowise.internship.userservice.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.CreditCardNumber;

import java.time.LocalDate;

@Data
public class AddCartRequestDto {

    @NotNull(message = "User ID cannot be null")
    private Long userId;

    @NotNull(message = "Card number cannot be empty")
    @CreditCardNumber(message = "Please provide a valid credit card number")
    private String number;

    @NotNull(message = "Card holder name cannot be empty")
    private String holder;

    @NotNull(message = "Expiration date cannot be null")
    @Future(message = "Expiration date must be in the future")
    private LocalDate expirationDate;
}
