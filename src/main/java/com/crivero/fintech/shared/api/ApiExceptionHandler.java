package com.crivero.fintech.shared.api;

import com.crivero.fintech.accounts.application.AccountService;
import com.crivero.fintech.accounts.domain.Account;
import com.crivero.fintech.shared.domain.exception.DomainException;
import com.crivero.fintech.transactions.application.TransferMoneyService;
import jakarta.validation.ConstraintViolationException;
import java.time.Instant;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {

  @ExceptionHandler(AccountService.AccountNotFound.class)
  public ResponseEntity<ErrorResponse> accountNotFound(AccountService.AccountNotFound ex) {
    return error(HttpStatus.NOT_FOUND, "ACCOUNT_NOT_FOUND", ex.getMessage());
  }

  @ExceptionHandler(TransferMoneyService.AccountNotFound.class)
  public ResponseEntity<ErrorResponse> transferAccountNotFound(TransferMoneyService.AccountNotFound ex) {
    return error(HttpStatus.NOT_FOUND, "ACCOUNT_NOT_FOUND", ex.getMessage());
  }

  @ExceptionHandler(Account.InsufficientFunds.class)
  public ResponseEntity<ErrorResponse> insufficientFunds(Account.InsufficientFunds ex) {
    return error(HttpStatus.CONFLICT, "INSUFFICIENT_FUNDS", ex.getMessage());
  }

  @ExceptionHandler({AccountService.InvalidAccountRequest.class, TransferMoneyService.InvalidTransfer.class})
  public ResponseEntity<ErrorResponse> badRequest(DomainException ex) {
    return error(HttpStatus.BAD_REQUEST, "BAD_REQUEST", ex.getMessage());
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> validation(MethodArgumentNotValidException ex) {
    return error(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "Request validation failed");
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<ErrorResponse> constraint(ConstraintViolationException ex) {
    return error(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", ex.getMessage());
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ErrorResponse> unreadable(HttpMessageNotReadableException ex) {
    return error(HttpStatus.BAD_REQUEST, "BAD_REQUEST", "Malformed JSON request");
  }

  private static ResponseEntity<ErrorResponse> error(HttpStatus status, String code, String message) {
    return ResponseEntity.status(status).body(new ErrorResponse(code, message, Instant.now()));
  }
}


