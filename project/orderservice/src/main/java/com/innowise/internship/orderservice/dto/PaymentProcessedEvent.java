package com.innowise.internship.orderservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentProcessedEvent {
    @NotBlank(message = "Payment ID cannot be blank")
    private String paymentId;

    @NotNull(message = "Order ID cannot be null")
    @Positive(message = "Order ID must be positive")
    private Long orderId;

    @NotBlank(message = "Payment status cannot be blank")
    @Pattern(regexp = "PENDING|COMPLETED|FAILED|REFUNDED", message = "Payment status must be one of: PENDING, COMPLETED, FAILED, REFUNDED")
    private String paymentStatus;
}
