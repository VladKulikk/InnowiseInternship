package com.innowise.internship.orderservice.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class CreateOrderDto {
    @NotNull(message = "User email cannot be null")
    @Email(message = "A valid user email is required")
    private String userEmail;

    @NotEmpty(message = "Order must contain at least one item")
    @Valid
    private List<OrderItemDto> orderItems;
}
