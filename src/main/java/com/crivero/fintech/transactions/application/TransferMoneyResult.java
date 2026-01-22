package com.crivero.fintech.transactions.application;

import java.time.Instant;
import java.util.Objects;

public record TransferMoneyResult(Long transactionId, Instant createdAt) {
  public TransferMoneyResult {
    Objects.requireNonNull(transactionId, "transactionId");
    Objects.requireNonNull(createdAt, "createdAt");
  }
}


