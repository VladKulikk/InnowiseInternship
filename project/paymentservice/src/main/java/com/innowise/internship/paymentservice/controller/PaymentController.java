package com.innowise.internship.paymentservice.controller;

import com.innowise.internship.paymentservice.model.Payment;
import com.innowise.internship.paymentservice.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping("/order/{orderId}")
    public ResponseEntity<List<Payment>> getPaymentsByOrderId(@PathVariable Long orderId) {
        List<Payment> payments = paymentService.getPaymentsByOrderId(orderId);
        return ResponseEntity.ok(payments);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Payment>> getPaymentsByUserId(@PathVariable Long userId) {
        List<Payment> payments = paymentService.getPaymentsByUserId(userId);
        return ResponseEntity.ok(payments);
    }

    @GetMapping("/status")
    public ResponseEntity<List<Payment>> getPaymentsByStatuses(@RequestParam("statuses") List<String> statuses) {
        List<Payment> payments = paymentService.getPaymentsByStatuses(statuses);
        return ResponseEntity.ok(payments);
    }

    @GetMapping("/stats/sum")
    public ResponseEntity<?> getPaymentsSumForPeriod(@RequestParam("startDate") Instant startDate, @RequestParam("endDate") Instant endDate) {
        BigDecimal total = paymentService.getTotalAmountForPeriod(startDate, endDate);
        return ResponseEntity.ok(java.util.Collections.singletonMap("totalAmount", total));
    }
}
