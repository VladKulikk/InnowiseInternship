package com.innowise.internship.paymentservice.service;

import com.innowise.internship.paymentservice.client.RandomNumberClient;
import com.innowise.internship.paymentservice.dto.PaymentStatsDto;
import com.innowise.internship.paymentservice.exception.ExternalServiceException;
import com.innowise.internship.paymentservice.exception.InvalidPaymentStatusException;
import com.innowise.internship.paymentservice.exception.PaymentDataCorruptException;
import com.innowise.internship.paymentservice.mapper.PaymentMapper;
import com.innowise.internship.paymentservice.model.Payment;
import com.innowise.internship.paymentservice.model.PaymentStatus;
import com.innowise.internship.paymentservice.repository.PaymentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceImplTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private RandomNumberClient randomNumberClient;

    @Mock
    private PaymentMapper paymentMapper;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    @Test
    void processPayment_shouldSetStatusCompleted_whenApiSucceeds(){
        Payment inputPayment = new Payment(1L, 100L, new BigDecimal("99.99"));
        ArgumentCaptor<Payment> paymentCaptor = ArgumentCaptor.forClass(Payment.class);

        when(randomNumberClient.getRandomNumber()).thenReturn(50);
        when(paymentRepository.save(any(Payment.class))).then(invocation -> invocation.getArgument(0));

        paymentService.processPayment(inputPayment);

        verify(paymentRepository, times(1)).save(paymentCaptor.capture());
        verify(randomNumberClient, times(1)).getRandomNumber();

        Payment savedPayment = paymentCaptor.getValue();

        assertThat(savedPayment.getPaymentStatus()).isEqualTo(PaymentStatus.COMPLETED);
        assertThat(savedPayment.getOrderId()).isEqualTo(1L);
    }

    @Test
    void processPayment_shouldSetStatusFailed_whenClientReturnsOdd(){
        Payment inputPayment = new Payment(1L, 100L, new BigDecimal("99.99"));
        ArgumentCaptor<Payment> paymentCaptor = ArgumentCaptor.forClass(Payment.class);

        when(randomNumberClient.getRandomNumber()).thenReturn(51);
        when(paymentRepository.save(any(Payment.class))).then(invocation -> invocation.getArgument(0));

        paymentService.processPayment(inputPayment);

        verify(randomNumberClient, times(1)).getRandomNumber();
        verify(paymentRepository, times(1)).save(paymentCaptor.capture());

        Payment savedPayment = paymentCaptor.getValue();

        assertThat(savedPayment.getPaymentStatus()).isEqualTo(PaymentStatus.FAILED);
    }

    @Test
    void processPayment_shouldSetStatusFailed_whenClientThrowsException(){
        Payment inputPayment = new Payment(1L, 100L, new BigDecimal("99.99"));
        ArgumentCaptor<Payment> paymentCaptor = ArgumentCaptor.forClass(Payment.class);

        when(randomNumberClient.getRandomNumber()).thenThrow(new ExternalServiceException("API is down"));
        when(paymentRepository.save(any(Payment.class))).then(invocation -> invocation.getArgument(0));

        paymentService.processPayment(inputPayment);

        verify(randomNumberClient, times(1)).getRandomNumber();
        verify(paymentRepository, times(1)).save(paymentCaptor.capture());

        Payment savedPayment = paymentCaptor.getValue();

        assertThat(savedPayment.getPaymentStatus()).isEqualTo(PaymentStatus.FAILED);
  }

    @Test
    void getPaymentsByOrderId_shouldReturnList(){
        List<Payment> mockList = List.of(new Payment(1L, 100L, BigDecimal.TEN));

        when(paymentRepository.findByOrderId(1L)).thenReturn(mockList);

        List<Payment> result = paymentService.getPaymentsByOrderId(1L);

        verify(paymentRepository, times(1)).findByOrderId(1L);
        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(1);
        assertThat(result).isEqualTo(mockList);
    }

    @Test
    void getPaymentsByUserId_shouldReturnList(){
        List<Payment> mockList = List.of(new Payment(1L, 100L, BigDecimal.TEN));

        when(paymentRepository.findByUserId(100L)).thenReturn(mockList);

        List<Payment> result = paymentService.getPaymentsByUserId(100L);

        verify(paymentRepository, times(1)).findByUserId(100L);
        assertThat(result).isEqualTo(mockList);
    }

    @Test
    void getPaymentsByStatuses_shouldCallMapperAndRepo(){
        List<String> statusStrings = List.of("COMPLETED", "FAILED");
        List<PaymentStatus> statusEnum = List.of(PaymentStatus.COMPLETED, PaymentStatus.FAILED);
        List<Payment> mockList = List.of(new Payment(1L, 100L, BigDecimal.TEN));

        when(paymentMapper.toStatusList(statusStrings)).thenReturn(statusEnum);
        when(paymentRepository.findByPaymentStatusIn(statusEnum)).thenReturn(mockList);

        List<Payment> result = paymentService.getPaymentsByStatuses(statusStrings);

        verify(paymentMapper, times(1)).toStatusList(statusStrings);
        verify(paymentRepository, times(1)).findByPaymentStatusIn(statusEnum);
        assertThat(result).isEqualTo(mockList);
    }

    @Test
    void getPaymentsByStatuses_shouldThrowIllegalArgumentException(){
        List<String> badStatusStrings = List.of("COMPLETED", "INVALID_STATUS");

    when(paymentMapper.toStatusList(badStatusStrings))
        .thenThrow(new InvalidPaymentStatusException("Invalid status"));

    assertThatThrownBy(() -> paymentService.getPaymentsByStatuses(badStatusStrings))
        .isInstanceOf(IllegalArgumentException.class);
        verify(paymentRepository, never()).findByPaymentStatusIn(anyList());
    }

    @Test
    void getTotalAmountForPeriod_shouldReturnTotal(){
        Instant startDate = Instant.now();
        Instant endDate = Instant.now().plusSeconds(60);
        PaymentStatsDto mockDto = new PaymentStatsDto();
        mockDto.setTotalAmount(new BigDecimal("99.99"));

        when(paymentRepository.getPaymentSumByDateRange(startDate, endDate)).thenReturn(mockDto);

        BigDecimal result = paymentService.getTotalAmountForPeriod(startDate, endDate);

        verify(paymentRepository, times(1)).getPaymentSumByDateRange(startDate, endDate);
        assertThat(result).isEqualTo(new BigDecimal("99.99"));
    }

    @Test
    void getTotalAmountForPeriod_shouldReturnZeroWhenDtoNull(){
        Instant startDate = Instant.now();
        Instant endDate = Instant.now().plusSeconds(60);

        when(paymentRepository.getPaymentSumByDateRange(startDate, endDate)).thenReturn(null);

        BigDecimal result = paymentService.getTotalAmountForPeriod(startDate, endDate);

        assertThat(result).isEqualTo(BigDecimal.ZERO);
    }

    @Test
    void getTotalAmountForPeriod_shouldThrowException_whenAmountNull(){
        Instant startDate = Instant.now();
        Instant endDate = Instant.now().plusSeconds(60);
        PaymentStatsDto mockDtoWithNullAmount = new PaymentStatsDto();
        mockDtoWithNullAmount.setTotalAmount(null);

        when(paymentRepository.getPaymentSumByDateRange(startDate, endDate)).thenReturn(mockDtoWithNullAmount);

        assertThatThrownBy(() -> paymentService.getTotalAmountForPeriod(startDate, endDate))
                .isInstanceOf(PaymentDataCorruptException.class)
                .hasMessageContaining("Payment data is corrupt. Aggregation returned null");
    }
}
