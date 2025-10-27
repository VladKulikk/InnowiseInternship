package com.innowise.internship.orderservice.exception;

public class InvalidRequestParametersException extends RuntimeException {
  public InvalidRequestParametersException(String message) {
    super(message);
  }
}
