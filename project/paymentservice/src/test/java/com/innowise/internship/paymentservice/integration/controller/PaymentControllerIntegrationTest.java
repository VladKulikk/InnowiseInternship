package com.innowise.internship.paymentservice.integration.controller;

import com.innowise.internship.paymentservice.controller.PaymentController;
import com.innowise.internship.paymentservice.exception.InvalidPaymentStatusException;
import com.innowise.internship.paymentservice.model.Payment;
import com.innowise.internship.paymentservice.model.PaymentStatus;
import com.innowise.internship.paymentservice.service.PaymentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PaymentController.class)
@TestPropertySource(properties = "mongock.enabled=false")
public class PaymentControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PaymentService paymentService;

    private Payment createTestPayment() {
        Payment payment = new Payment(1L, 100L, new BigDecimal("50.00"));
        payment.setId("test-mongo-id-123");
        payment.setPaymentStatus(PaymentStatus.COMPLETED);
        payment.setTimestamp(Instant.parse("2025-11-15T10:00:00Z"));
        return payment;
    }

    @Test
    void testGetPaymentsByOrderId() throws Exception {
        Payment payment = createTestPayment();

        when(paymentService.getPaymentsByOrderId(1L)).thenReturn(List.of(payment));

        mockMvc
                .perform(get("/api/v1/payments/order/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()", is(1)))
                .andExpect(jsonPath("$[0].orderId", is(1)))
                .andExpect(jsonPath("$[0].id", is("test-mongo-id-123")));
    }

    @Test
    void testGetPaymentsByUserId() throws Exception {
        Payment payment = createTestPayment();

        when(paymentService.getPaymentsByUserId(100L)).thenReturn(List.of(payment));

        mockMvc
                .perform(get("/api/v1/payments/user/100"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()", is(1)))
                .andExpect(jsonPath("$[0].userId", is(100)));
    }

    @Test
    void testGetPaymentsByStatuses() throws Exception {
        List<String> statuses = List.of("COMPLETED", "PENDING");
        Payment payment = createTestPayment();

        when(paymentService.getPaymentsByStatuses(statuses)).thenReturn(List.of(payment));

        mockMvc
                .perform(get("/api/v1/payments/status").param("statuses", "COMPLETED", "PENDING"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()", is(1)))
                .andExpect(jsonPath("$[0].paymentStatus", is("COMPLETED")));
    }

    @Test
    void testGetPaymentsSumForPeriod() throws Exception {
        Instant start = Instant.parse("2025-01-01T00:00:00Z");
        Instant end = Instant.parse("2025-01-31T23:59:59Z");

        BigDecimal total = new BigDecimal("123.45");

        when(paymentService.getTotalAmountForPeriod(start, end)).thenReturn(total);

        mockMvc
                .perform(
                        get("/api/v1/payments/stats/sum")
                                .param("startDate", "2025-01-01T00:00:00Z")
                                .param("endDate", "2025-01-31T23:59:59Z"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.totalAmount", is(123.45)));
    }

    @Test
    void testGetPaymentsByStatuses_invalidStatus() throws Exception {
        String invalidStatus = "INVALID_STATUS";

    when(paymentService.getPaymentsByStatuses(List.of(invalidStatus)))
            .thenThrow(
                    new InvalidPaymentStatusException("Invalid payment status provided: " + invalidStatus));

        mockMvc
                .perform(get("/api/v1/payments/status").param("statuses", invalidStatus))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.error", is("Bad Request")))
                .andExpect(jsonPath("$.message", is("Invalid payment status provided: " + invalidStatus)));
  }
}
