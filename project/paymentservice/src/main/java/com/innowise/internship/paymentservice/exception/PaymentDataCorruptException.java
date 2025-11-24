package com.innowise.internship.paymentservice.exception;

public class PaymentDataCorruptException extends RuntimeException {
    public PaymentDataCorruptException(String message) {
        super(message);
    }
}
