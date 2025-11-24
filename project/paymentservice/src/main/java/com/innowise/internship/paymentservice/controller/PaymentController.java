package com.innowise.internship.paymentservice.controller;

import com.innowise.internship.paymentservice.dto.PaymentResponseDto;
import com.innowise.internship.paymentservice.dto.PaymentStatsDto;
import com.innowise.internship.paymentservice.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping("/order/{orderId}")
    public ResponseEntity<List<PaymentResponseDto>> getPaymentsByOrderId(@PathVariable Long orderId) {
        List<PaymentResponseDto> payments = paymentService.getPaymentsByOrderId(orderId);
        return ResponseEntity.ok(payments);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PaymentResponseDto>> getPaymentsByUserId(@PathVariable Long userId) {
        List<PaymentResponseDto> payments = paymentService.getPaymentsByUserId(userId);
        return ResponseEntity.ok(payments);
    }

    @GetMapping("/status")
    public ResponseEntity<List<PaymentResponseDto>> getPaymentsByStatuses(@RequestParam("statuses") List<String> statuses) {
        List<PaymentResponseDto> payments = paymentService.getPaymentsByStatuses(statuses);
        return ResponseEntity.ok(payments);
    }

    @GetMapping("/stats/sum")
    public ResponseEntity<PaymentStatsDto> getPaymentsSumForPeriod(@RequestParam("startDate") Instant startDate, @RequestParam("endDate") Instant endDate) {
        PaymentStatsDto statsDto = paymentService.getTotalAmountForPeriod(startDate, endDate);
        return ResponseEntity.ok(statsDto);
    }
}
