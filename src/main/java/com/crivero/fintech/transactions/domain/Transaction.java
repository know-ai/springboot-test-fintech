package com.crivero.fintech.transactions.domain;

import com.crivero.fintech.shared.domain.Money;
import java.time.Instant;
import java.util.Objects;

/**
 * Modelo de dominio para una transacción de transferencia.
 */
public final class Transaction {
  private final Long id;
  private final Long sourceAccountId;
  private final Long destinationAccountId;
  private final Money amount;
  private final Instant createdAt;

  private Transaction(Long id, Long sourceAccountId, Long destinationAccountId, Money amount, Instant createdAt) {
    this.id = id;
    this.sourceAccountId = Objects.requireNonNull(sourceAccountId, "sourceAccountId");
    this.destinationAccountId = Objects.requireNonNull(destinationAccountId, "destinationAccountId");
    this.amount = Objects.requireNonNull(amount, "amount");
    this.createdAt = Objects.requireNonNull(createdAt, "createdAt");
  }

  public static Transaction of(Long id, Long sourceAccountId, Long destinationAccountId, Money amount, Instant createdAt) {
    return new Transaction(id, sourceAccountId, destinationAccountId, amount, createdAt);
  }

  public Long id() {
    return id;
  }

  public Long sourceAccountId() {
    return sourceAccountId;
  }

  public Long destinationAccountId() {
    return destinationAccountId;
  }

  public Money amount() {
    return amount;
  }

  public Instant createdAt() {
    return createdAt;
  }
}


