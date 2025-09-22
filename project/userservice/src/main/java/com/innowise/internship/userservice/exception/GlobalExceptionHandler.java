package com.innowise.internship.userservice.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionHandler {

  // Handler for custom exception
  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleResourceNotFoundException(
      ResourceNotFoundException ex, HttpServletRequest request) {

    ErrorResponse errorResponse =
        new ErrorResponse(
            LocalDateTime.now(),
            HttpStatus.NOT_FOUND.value(),
            "Not found",
            ex.getMessage(),
            request.getRequestURI());

    return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(DuplicateResourceException.class)
  public ResponseEntity<ErrorResponse> handleDuplicateResourseException(
          DuplicateResourceException ex, HttpServletRequest request) {

    ErrorResponse errorResponse =
        new ErrorResponse(
            LocalDateTime.now(),
            HttpStatus.CONFLICT.value(),
            "Duplicate resourse",
            ex.getMessage(),
            request.getRequestURI());

    return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(
      MethodArgumentNotValidException ex, HttpServletRequest request) {

    ErrorResponse errorResponse =
        new ErrorResponse(
            LocalDateTime.now(),
            HttpStatus.BAD_REQUEST.value(),
            "Bad Request",
            ex.getMessage(),
            request.getRequestURI());

    return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
  }
}
