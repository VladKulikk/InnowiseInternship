package com.innowise.internship.paymentservice.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Document(collection = "payments")
public class Payment {

    @Id
    private String id;

    @Field("order_id")
    private Long orderId;

    @Field("user_id")
    private Long userId;

    private PaymentStatus paymentStatus;

    private Instant timestamp;

    @Field(value = "payment_amount", targetType = FieldType.DECIMAL128)
    private BigDecimal paymentAmount;

    public Payment(Long orderId, Long userId, BigDecimal paymentAmount) {
        this.orderId = orderId;
        this.paymentAmount = paymentAmount;
        this.userId = userId;
        this.paymentStatus = PaymentStatus.PENDING;
        this.timestamp = Instant.now();
    }
}
