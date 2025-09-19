package com.innowise.internship.userservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class DuplicateResourseException extends RuntimeException {
  public DuplicateResourseException(String message) {
    super(message);
  }
}
