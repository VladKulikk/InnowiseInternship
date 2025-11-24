package com.innowise.internship.paymentservice.service;

import com.innowise.internship.paymentservice.dto.PaymentResponseDto;
import com.innowise.internship.paymentservice.dto.PaymentStatsDto;
import com.innowise.internship.paymentservice.model.Payment;

import java.time.Instant;
import java.util.List;

public interface PaymentService {
    Payment processPayment(Payment payment);

    List<PaymentResponseDto> getPaymentsByOrderId(Long orderId);

    List<PaymentResponseDto> getPaymentsByUserId(Long userId);

    List<PaymentResponseDto> getPaymentsByStatuses(List<String> statuses);

    PaymentStatsDto getTotalAmountForPeriod(Instant startDate, Instant endDate);
}
