package com.crivero.fintech.transactions.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "transactions")
public class TransactionEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private Long sourceAccountId;

  @Column(nullable = false)
  private Long destinationAccountId;

  @Column(nullable = false, precision = 19, scale = 2)
  private BigDecimal amount;

  @Column(nullable = false)
  private Instant createdAt;

  protected TransactionEntity() {}

  public TransactionEntity(Long sourceAccountId, Long destinationAccountId, BigDecimal amount) {
    this.sourceAccountId = sourceAccountId;
    this.destinationAccountId = destinationAccountId;
    this.amount = amount;
    this.createdAt = Instant.now();
  }

  public Long getId() {
    return id;
  }

  public Long getSourceAccountId() {
    return sourceAccountId;
  }

  public Long getDestinationAccountId() {
    return destinationAccountId;
  }

  public BigDecimal getAmount() {
    return amount;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }
}


