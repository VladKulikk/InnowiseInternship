package com.innowise.internship.paymentservice.dto;

import com.innowise.internship.paymentservice.model.PaymentStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

@Data
public class PaymentResponseDto {
    private String id;
    private Long orderId;
    private Long userId;
    private PaymentStatus paymentStatus;
    private Instant timestamp;
    private BigDecimal paymentAmount;
}
