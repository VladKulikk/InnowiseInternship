package com.innowise.internship.paymentservice.service;

import com.innowise.internship.paymentservice.model.Payment;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public interface PaymentService {
    Payment processPayment(Payment payment);

    List<Payment> getPaymentsByOrderId(Long orderId);

    List<Payment> getPaymentsByUserId(Long userId);

    List<Payment> getPaymentsByStatuses(List<String> statuses);

    BigDecimal getTotalAmountForPeriod(Instant startDate, Instant endDate);
}
