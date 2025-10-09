package com.innowise.internship.orderservice.dto;

import com.innowise.internship.orderservice.model.OrderStatus;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class OrderResponseDto {
    private Long id;
    private OrderStatus status;
    private LocalDate creationDate;
    private List<OrderItemDto> orderItems;
    private UserResponseDto user;
}
