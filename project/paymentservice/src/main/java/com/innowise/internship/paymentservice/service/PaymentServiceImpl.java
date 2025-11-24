package com.innowise.internship.paymentservice.service;

import com.innowise.internship.paymentservice.client.RandomNumberClient;
import com.innowise.internship.paymentservice.dto.PaymentResponseDto;
import com.innowise.internship.paymentservice.dto.PaymentStatsDto;
import com.innowise.internship.paymentservice.exception.ExternalServiceException;
import com.innowise.internship.paymentservice.mapper.PaymentMapper;
import com.innowise.internship.paymentservice.model.Payment;
import com.innowise.internship.paymentservice.model.PaymentStatus;
import com.innowise.internship.paymentservice.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.bson.types.Decimal128;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final RandomNumberClient randomNumberClient;
    private final PaymentMapper paymentMapper;

    @Override
    public Payment processPayment(Payment payment) {
        try {
            int number = randomNumberClient.getRandomNumber();

            boolean isSuccess = (number % 2 == 0);

            if (isSuccess) {
                payment.setPaymentStatus(PaymentStatus.COMPLETED);
            } else {
                payment.setPaymentStatus(PaymentStatus.FAILED);
            }
        } catch (ExternalServiceException e) {
            payment.setPaymentStatus(PaymentStatus.FAILED);
        }
        return paymentRepository.save(payment);
    }

    @Override
    public List<PaymentResponseDto> getPaymentsByOrderId(Long orderId) {
        return paymentMapper.toPaymentResponseDtoList(paymentRepository.findByOrderId(orderId));
    }

    @Override
    public List<PaymentResponseDto> getPaymentsByUserId(Long userId) {
        return paymentMapper.toPaymentResponseDtoList(paymentRepository.findByUserId(userId));
    }

    @Override
    public List<PaymentResponseDto> getPaymentsByStatuses(List<String> statuses) {
        List<PaymentStatus> statusesList = paymentMapper.toStatusList(statuses);
        return paymentMapper.toPaymentResponseDtoList(paymentRepository.findByPaymentStatusIn(statusesList));
    }

    @Override
    public PaymentStatsDto getTotalAmountForPeriod(Instant startDate, Instant endDate) {
        Decimal128 result = paymentRepository.getPaymentSumByDateRange(startDate, endDate);

        BigDecimal totalAmount = result != null ? result.bigDecimalValue() : BigDecimal.ZERO;
        return new PaymentStatsDto(totalAmount);
    }
}
