package com.innowise.internship.paymentservice.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PaymentStatsDto {
    private BigDecimal totalAmount;
}
