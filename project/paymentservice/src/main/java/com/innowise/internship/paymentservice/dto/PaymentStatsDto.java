package com.innowise.internship.paymentservice.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@Data
@RequiredArgsConstructor
public class PaymentStatsDto {
    private BigDecimal totalAmount;
}
