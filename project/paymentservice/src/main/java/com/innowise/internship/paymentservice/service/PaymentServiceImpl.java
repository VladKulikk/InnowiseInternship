package com.innowise.internship.paymentservice.service;

import com.innowise.internship.paymentservice.client.RandomNumberClient;
import com.innowise.internship.paymentservice.dto.PaymentStatsDto;
import com.innowise.internship.paymentservice.exception.ExternalServiceException;
import com.innowise.internship.paymentservice.exception.PaymentDataCorruptException;
import com.innowise.internship.paymentservice.mapper.PaymentMapper;
import com.innowise.internship.paymentservice.model.Payment;
import com.innowise.internship.paymentservice.model.PaymentStatus;
import com.innowise.internship.paymentservice.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
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
  public List<Payment> getPaymentsByOrderId(Long orderId) {
    return paymentRepository.findByOrderId(orderId);
  }

  @Override
  public List<Payment> getPaymentsByUserId(Long userId) {
    return paymentRepository.findByUserId(userId);
  }

  @Override
  public List<Payment> getPaymentsByStatuses(List<String> statuses) {
    List<PaymentStatus> statusesList = paymentMapper.toStatusList(statuses);
    return paymentRepository.findByPaymentStatusIn(statusesList);
  }

  @Override
  public BigDecimal getTotalAmountForPeriod(Instant startDate, Instant endDate) {
    PaymentStatsDto stats = paymentRepository.getPaymentSumByDateRange(startDate, endDate);

    if (stats == null) {
      return BigDecimal.ZERO;
    }

    if (stats.getTotalAmount() == null) {
      throw new PaymentDataCorruptException("Payment data is corrupt. Aggregation returned null");
    }

    return stats.getTotalAmount();
  }
}
