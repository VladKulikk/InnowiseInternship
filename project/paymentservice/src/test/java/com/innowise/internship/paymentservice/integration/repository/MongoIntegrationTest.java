package com.innowise.internship.paymentservice.integration.repository;

import com.innowise.internship.paymentservice.dto.PaymentStatsDto;
import com.innowise.internship.paymentservice.model.Payment;
import com.innowise.internship.paymentservice.model.PaymentStatus;
import com.innowise.internship.paymentservice.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.testcontainers.utility.TestcontainersConfiguration;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
@Import(TestcontainersConfiguration.class)
public class MongoIntegrationTest {

    @Autowired
    private PaymentRepository paymentRepository;

    private Payment p1, p2, p3;
    private final Instant now =  Instant.now();

    @BeforeEach
    void setup(){
        paymentRepository.deleteAll();

        p1 = new Payment(1L, 100L, new BigDecimal("10.00"));
        p1.setPaymentStatus(PaymentStatus.COMPLETED);
        p1.setTimestamp(now.minus(1, ChronoUnit.DAYS));

        p2 = new Payment(1L, 100L, new BigDecimal("20.00"));
        p2.setPaymentStatus(PaymentStatus.FAILED);
        p2.setTimestamp(now);

        p3 = new Payment(2L, 200L, new BigDecimal("30.00"));
        p3.setPaymentStatus(PaymentStatus.COMPLETED);
        p3.setTimestamp(now);

        paymentRepository.saveAll(List.of(p1, p2, p3));
    }

    @Test
    void testFindByOrderId(){
        List<Payment> results = paymentRepository.findByOrderId(1L);

        assertThat(results).hasSize(2);
        assertThat(results).extracting(Payment::getId).contains(p1.getId(), p2.getId());
    }

    @Test
    void testFindByUserId(){
        List<Payment> results = paymentRepository.findByUserId(100L);

        assertThat(results).hasSize(2);
        assertThat(results).extracting(Payment::getId).contains(p1.getId(),  p2.getId());
    }

    @Test
    void testFindByPaymentStatusIn(){
        List<Payment> results = paymentRepository.findByPaymentStatusIn(List.of(PaymentStatus.COMPLETED));

        assertThat(results).hasSize(2);
        assertThat(results).extracting(Payment::getId).contains(p1.getId(), p3.getId());
    }

    @Test
    void testGetPaymentSumByDateRange(){
        Instant startTime = now.minus(1, ChronoUnit.HOURS);
        Instant endTime = now.plus(1, ChronoUnit.HOURS);

        PaymentStatsDto stats = paymentRepository.getPaymentSumByDateRange(startTime, endTime);

        assertThat(stats).isNotNull();
        assertThat(stats.getTotalAmount()).isEqualByComparingTo("50.00");
    }

    @Test
    void testGetPaymentSumByDateRange_NoResult(){
        Instant startDate = now.minus(10, ChronoUnit.DAYS);
        Instant endDate = now.minus(9, ChronoUnit.DAYS);

        PaymentStatsDto stats = paymentRepository.getPaymentSumByDateRange(startDate, endDate);

        assertThat(stats).isNull();
    }
}
