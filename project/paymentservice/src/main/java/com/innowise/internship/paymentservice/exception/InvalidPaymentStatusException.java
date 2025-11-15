package com.innowise.internship.paymentservice.exception;

public class InvalidPaymentStatusException extends IllegalArgumentException {
  public InvalidPaymentStatusException(String message) {
    super(message);
  }
}
