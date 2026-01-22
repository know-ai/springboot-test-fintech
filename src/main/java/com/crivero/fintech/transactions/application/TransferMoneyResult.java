package com.crivero.fintech.transactions.application;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

public record TransferMoneyResult(
    Long transactionId,
    Long sourceAccountId,
    Long destinationAccountId,
    BigDecimal amount,
    Instant createdAt
) {
  public TransferMoneyResult {
    Objects.requireNonNull(transactionId, "transactionId");
    Objects.requireNonNull(sourceAccountId, "sourceAccountId");
    Objects.requireNonNull(destinationAccountId, "destinationAccountId");
    Objects.requireNonNull(amount, "amount");
    Objects.requireNonNull(createdAt, "createdAt");
  }
}


